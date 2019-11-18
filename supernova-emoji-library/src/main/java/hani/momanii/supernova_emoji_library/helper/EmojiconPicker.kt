package hani.momanii.supernova_emoji_library.helper

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.support.v4.view.ViewPager.OnPageChangeListener
import android.view.LayoutInflater
import android.view.View
import android.view.View.MeasureSpec
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams
import android.widget.ImageButton
import android.widget.LinearLayout
import hani.momanii.supernova_emoji_library.R
import hani.momanii.supernova_emoji_library.emoji.Cars
import hani.momanii.supernova_emoji_library.emoji.Electr
import hani.momanii.supernova_emoji_library.emoji.Food
import hani.momanii.supernova_emoji_library.emoji.Nature
import hani.momanii.supernova_emoji_library.emoji.People
import hani.momanii.supernova_emoji_library.emoji.Sport
import hani.momanii.supernova_emoji_library.emoji.Symbols
import hani.momanii.supernova_emoji_library.helper.EmojiconGridView.OnEmojiconClickedListener
import java.util.Arrays

class EmojiconPicker : OnPageChangeListener {
    private lateinit var popupWindow: AlertDialog
    private lateinit var mRecentsManager: EmojiconRecentsManager
    private var mEmojiTabLastSelectedIndex = -1
    private var mEmojiTabs: Array<View?> = emptyArray()
    private var iconPressedColor = "#495C66"
    private var tabsColor = "#DCE1E2"
    private var backgroundColor = "#E6EBEF"
    private var mEmojiSelectedListener: EmojiconPicker.OnSelectedListener? = null

    fun show(anchor: View) {
        if (!popupWindow.isShowing) {
            popupWindow.show()
        }
    }

    fun dismiss() {
        if (popupWindow.isShowing)
            popupWindow.dismiss()
    }

    fun setOnEmojiSelectedListener(listener: EmojiconPicker.OnSelectedListener) {
        this.mEmojiSelectedListener = listener
    }

    fun create(ctx: Context, useSystemDefault: Boolean = true) {
        val view = LayoutInflater.from(ctx).inflate(R.layout.emojicons, null, false)

        popupWindow = AlertDialog.Builder(ctx)
            .setView(view)
            .create()

        view.layoutParams = WindowManager.LayoutParams()
        view.layoutParams.height = LayoutParams.WRAP_CONTENT

        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
        view.layoutParams.height = view.width
        view.layoutParams.height = view.width

        val emojisPager = view.findViewById(R.id.emojis_pager) as ViewPager
        val tabs = view.findViewById(R.id.emojis_tab) as LinearLayout

        emojisPager.setOnPageChangeListener(this)
        val recents = EmojiconRecents { context, emojicon ->
            (emojisPager.adapter as? EmojisPagerAdapter?)?.recentFragment?.addRecentEmoji(context, emojicon)
        }
        val clickListener: OnEmojiconClickedListener = OnEmojiconClickedListener {
            mEmojiSelectedListener?.onSelected(view, it.emoji)
            dismiss()
        }
        val mEmojisAdapter = EmojisPagerAdapter(
            Arrays.asList(
                EmojiconRecentsGridView(ctx, null, null, clickListener, useSystemDefault),
                EmojiconGridView(ctx, People.DATA, recents, clickListener, useSystemDefault),
                EmojiconGridView(ctx, Nature.DATA, recents, clickListener, useSystemDefault),
                EmojiconGridView(ctx, Food.DATA, recents, clickListener, useSystemDefault),
                EmojiconGridView(ctx, Sport.DATA, recents, clickListener, useSystemDefault),
                EmojiconGridView(ctx, Cars.DATA, recents, clickListener, useSystemDefault),
                EmojiconGridView(ctx, Electr.DATA, recents, clickListener, useSystemDefault),
                EmojiconGridView(ctx, Symbols.DATA, recents, clickListener, useSystemDefault)

            )
        )
        emojisPager.adapter = mEmojisAdapter
        mEmojiTabs = arrayOfNulls(8)

        mEmojiTabs[0] = view.findViewById(R.id.emojis_tab_0_recents)
        mEmojiTabs[1] = view.findViewById(R.id.emojis_tab_1_people)
        mEmojiTabs[2] = view.findViewById(R.id.emojis_tab_2_nature)
        mEmojiTabs[3] = view.findViewById(R.id.emojis_tab_3_food)
        mEmojiTabs[4] = view.findViewById(R.id.emojis_tab_4_sport)
        mEmojiTabs[5] = view.findViewById(R.id.emojis_tab_5_cars)
        mEmojiTabs[6] = view.findViewById(R.id.emojis_tab_6_elec)
        mEmojiTabs[7] = view.findViewById(R.id.emojis_tab_7_sym)
        mEmojiTabs.forEachIndexed { i, tab ->
            tab?.setOnClickListener(OnClickListener { emojisPager.currentItem = i })
        }

        emojisPager.setBackgroundColor(Color.parseColor(backgroundColor))
        tabs.setBackgroundColor(Color.parseColor(tabsColor))
        for (x in mEmojiTabs.indices) {
            val btn = mEmojiTabs[x] as ImageButton
            btn.setColorFilter(Color.parseColor(iconPressedColor))
        }

        view.findViewById<View>(R.id.emojis_backspace)?.visibility = View.GONE
//
        // get last selected page
        mRecentsManager = EmojiconRecentsManager.getInstance(view.context)
        var page = 0 //mRecentsManager.getRecentPage()
        // last page was recents, check if there are recents to use
        // if none was found, go to page 1
        if (page == 0 && mRecentsManager.size == 0) {
            page = 1
        }

        if (page == 0) {
            onPageSelected(page)
        } else {
            emojisPager.setCurrentItem(page, false)
        }
    }

    override fun onPageScrollStateChanged(p0: Int) {
    }

    override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
    }

    override fun onPageSelected(i: Int) {
        if (mEmojiTabLastSelectedIndex == i) {
            return
        }
        when (i) {
            0, 1, 2, 3, 4, 5, 6, 7 -> {
                if (mEmojiTabLastSelectedIndex >= 0 && mEmojiTabLastSelectedIndex < mEmojiTabs.size) {
                    mEmojiTabs[mEmojiTabLastSelectedIndex]?.isSelected = false
                }
                mEmojiTabs[i]?.isSelected = true
                mEmojiTabLastSelectedIndex = i
//                        mRecentsManager.setRecentPage(i)
            }
        }
    }

    private class EmojisPagerAdapter(private val views: List<EmojiconGridView>) : PagerAdapter() {
        val recentFragment: EmojiconRecentsGridView?
            get() {
                for (it in views) {
                    if (it is EmojiconRecentsGridView)
                        return it
                }
                return null
            }

        override fun getCount(): Int {
            return views.size
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val v = views[position].rootView
            (container as ViewPager).addView(v, 0)
            return v
        }

        override fun destroyItem(container: ViewGroup, position: Int, view: Any) {
            (container as ViewPager).removeView(view as View)
        }

        override fun isViewFromObject(view: View, key: Any): Boolean {
            return key === view
        }
    }

    interface OnSelectedListener {
        fun onSelected(view: View, emoji: String)
    }
}