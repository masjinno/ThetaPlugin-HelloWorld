/**
 * Copyright 2018 Ricoh Company, Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.theta360.pluginapplication;

import android.os.Bundle;
import android.view.KeyEvent;
import com.theta360.pluginapplication.task.TakePictureTask;
import com.theta360.pluginapplication.task.TakePictureTask.Callback;
import com.theta360.pluginlibrary.activity.PluginActivity;
import com.theta360.pluginlibrary.callback.KeyCallback;
import com.theta360.pluginlibrary.receiver.KeyReceiver;
import com.theta360.pluginlibrary.values.LedColor;
import com.theta360.pluginlibrary.values.LedTarget;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends PluginActivity {

    // スケジュール時間間隔
    private static final int TIMER_INTERVAL_PERIOD = 250;

    private TakePictureTask.Callback mTakePictureTaskCallback = new Callback() {
        @Override
        public void onTakePicture(String fileUrl) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ランプ更新用タイマーの宣言
        Timer timer = new Timer();
        long delay = 0;
        Random ledColorRand = new Random();
        Random ledTargetRand = new Random();
        Random ledHideRand = new Random();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int ledColorNum = ledColorRand.nextInt(LedColor.values().length);
                int ledTargetNum = ledTargetRand.nextInt(LedTarget.values().length);
                int ledHideNum = ledHideRand.nextInt(LedTarget.values().length);
                notificationLed3Show(LedColor.values()[ledColorNum]);
                notificationLedShow(LedTarget.values()[ledTargetNum]);
                notificationLedHide(LedTarget.values()[ledHideNum]);
            }
        }, delay, TIMER_INTERVAL_PERIOD);

        // Set a callback when a button operation event is acquired.
        setKeyCallback(new KeyCallback() {
            @Override
            public void onKeyDown(int keyCode, KeyEvent event) {
                if (keyCode == KeyReceiver.KEYCODE_CAMERA) {
                    // 写真を撮らないので、写真を撮るための処理をコメントアウトする
                    /*
                     * To take a static picture, use the takePicture method.
                     * You can receive a fileUrl of the static picture in the callback.
                     */
                    /* new TakePictureTask(mTakePictureTaskCallback).execute(); */

                    // ランダム色点灯処理を、シャッターボタンを押すことでキャンセル
                    timer.cancel();
                    // ランプを消灯させる
                    List<LedTarget> ledHideTargets = Arrays.asList(
                            LedTarget.LED3,
                            LedTarget.LED4,
                            LedTarget.LED5,
                            LedTarget.LED6,
                            LedTarget.LED7,
                            LedTarget.LED8
                    );
                    ledHideTargets.forEach(led -> {
                        notificationLedHide(led);
                    });
                }
            }

            @Override
            public void onKeyUp(int keyCode, KeyEvent event) {
                // シャッターボタンを押したときに、ランプを点灯をさせない
                /**
                 * You can control the LED of the camera.
                 * It is possible to change the way of lighting, the cycle of blinking, the color of light emission.
                 * Light emitting color can be changed only LED3.
                 */
                /* notificationLedBlink(LedTarget.LED3, LedColor.BLUE, 1000); */
            }

            @Override
            public void onKeyLongPress(int keyCode, KeyEvent event) {
                // 長押ししたときは、プラグイン終了
                /* notificationError(""); */

                // Timerを終了させることで、一定間隔でランダムに色を点滅させる処理を終了
                timer.cancel();
                // ランプ消灯
                List<LedTarget> ledHideTargets = Arrays.asList(
                        LedTarget.LED3,
                        LedTarget.LED4,
                        LedTarget.LED5,
                        LedTarget.LED6,
                        LedTarget.LED7,
                        LedTarget.LED8
                );
                ledHideTargets.forEach(led -> {
                    notificationLedHide(led);
                });

                // 正常にプラグインが終了したときの通知
                notificationSuccess();
            }
        });
    }
}
