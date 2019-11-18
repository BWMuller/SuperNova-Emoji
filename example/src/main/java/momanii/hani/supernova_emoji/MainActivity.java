package momanii.hani.supernova_emoji;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import hani.momanii.supernova_emoji_library.actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.helper.EmojiconPicker;
import hani.momanii.supernova_emoji_library.helper.EmojiconTextView;

public class MainActivity extends AppCompatActivity {

    CheckBox mCheckBox;
    EmojiconEditText emojiconEditText, emojiconEditText2;
    EmojiconTextView textView;
    ImageView emojiButton;
    ImageView submitButton;
    View rootView;
    EmojIconActions emojIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootView = findViewById(R.id.root_view);
        emojiButton = (ImageView) findViewById(R.id.emoji_btn);
        Button dialog_trigger = (Button) findViewById(R.id.dialog_trigger);
        submitButton = (ImageView) findViewById(R.id.submit_btn);
        mCheckBox = (CheckBox) findViewById(R.id.use_system_default);
        emojiconEditText = (EmojiconEditText) findViewById(R.id.emojicon_edit_text);
        emojiconEditText2 = (EmojiconEditText) findViewById(R.id.emojicon_edit_text2);
        textView = (EmojiconTextView) findViewById(R.id.textView);
        emojIcon = new EmojIconActions(this, rootView, emojiconEditText, emojiButton);
        emojIcon.ShowEmojIcon();
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("Keyboard", "open");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("Keyboard", "close");
            }
        });

        mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                emojIcon.setUseSystemEmoji(b);
                textView.setUseSystemDefault(b);
            }
        });
        emojIcon.addEmojiconEditTextList(emojiconEditText2);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newText = emojiconEditText.getText().toString();
                textView.setText(newText);
            }
        });

        dialog_trigger.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(final View v) {
                EmojiconPicker picker = new EmojiconPicker();
                picker.create(v.getContext(), true);
                picker.setOnEmojiSelectedListener(new EmojiconPicker.OnSelectedListener() {
                    @Override
                    public void onSelected(@NonNull View view, @NonNull String emoji) {
                        textView.setText(emoji);
                    }
                });
                picker.show(v);

            }
        });
    }


}
