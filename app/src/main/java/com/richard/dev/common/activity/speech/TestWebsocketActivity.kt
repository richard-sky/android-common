package com.richard.dev.common.activity.speech

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.lifecycleScope
import com.alibaba.fastjson.TypeReference
import com.richard.dev.common.R
import com.richard.dev.common.activity.speech.AudioVadRecorder.RecorderCallback
import com.richard.dev.common.activity.speech.model.BasicSpeechReply
import com.richard.dev.common.activity.speech.model.SpeechTextResult
import com.richard.ilbrary.compose.widget.FButton
import com.richard.ilbrary.compose.widget.PageScaffold
import com.richard.library.basic.basic.BasicActivity
import com.richard.library.context.util.URLUtil
import com.richard.library.context.util.isNotNull
import com.richard.library.context.util.isNull
import com.richard.library.context.util.media.AudioItem
import com.richard.library.context.util.media.AudioSourceType
import com.richard.library.context.util.media.MediaPlayerUtil
import com.richard.library.context.util.media.TTSSpeaker
import com.richard.library.context.util.startsWith
import com.richard.library.net.websocket.WebSocketClient
import com.richard.library.permission.PermissionRequester
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID


/**
 * @author: Richard
 * @createDate: 2026/6/25 12:02
 * @version: 1.0
 * @description: Websocket测试页面
 */
class TestWebsocketActivity : BasicActivity() {

    var webSocketClient: WebSocketClient? = null
    val player = MediaPlayerUtil()

    // 录音VAD工具实例
    private lateinit var speechRecorder: AudioVadRecorder
    private val REQUEST_RECORD_PERM = 1001


    companion object {
        fun start(context: Context) {
            context.startActivity(Intent(context, TestWebsocketActivity::class.java))
        }
    }

    override fun initLayoutView() {
        setContent {
            PageScaffold(titleText = "Websocket Demo", backEvent = { onBackPressed() }) {
                UI()
            }
        }
    }

    override fun initData() {
        //https://0110.be/releases/TarsosDSP/
        speechRecorder = AudioVadRecorder.create(this)
            .silenceThreshDb(-80.0)
            .saveWavEnable(true)
            .build()

        speechRecorder.setCallback(object : RecorderCallback {
            override fun onVoiceStateChange(hasVoice: Boolean, currentDB: Double) {
                // UI更新分贝、人声状态
                Log.d("VAD", "是否有人声：$hasVoice DB：$currentDB")
            }

            override fun onRealTimePcm(pcmBytes: ByteArray?) {
                // 实时原始PCM流，可用于实时语音识别、传输
            }

            override fun onSilenceAutoStop() {
                Log.d("VAD", "静音自动结束录音")
            }

            override fun onWavSaved(wavPath: String?) {
                // 文件保存成功回调
                Log.d("VAD", "录音文件路径：$wavPath")
            }

            override fun onError(msg: String?, e: Exception?) {
                Log.d("VAD", "录音onError：$e")
            }
        })
    }

    override fun bindListener() {
    }

    @Composable
    private fun UI() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.content_padding)),
        ) {

            FButton(text = "连接语音服务器", modifier = Modifier.fillMaxWidth()) {
                connect()
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_item_margin)))

            FButton(text = "断开连接", modifier = Modifier.fillMaxWidth()) {
                webSocketClient?.disconnect()
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_item_margin)))

            FButton(text = "发送文本对话", modifier = Modifier.fillMaxWidth()) {
                sendText()
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_item_margin)))

            FButton(text = "发送意图对话", modifier = Modifier.fillMaxWidth()) {
                sendIntent()
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_item_margin)))

            FButton(text = "发送语音对话", modifier = Modifier.fillMaxWidth()) {
                sendVoice()
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_item_margin)))

            FButton(text = "开始录音并播放", modifier = Modifier.fillMaxWidth()) {
                PermissionRequester.with(this@TestWebsocketActivity)
                    .permission(android.Manifest.permission.RECORD_AUDIO)
                    .request {
                        TTSSpeaker.getInstance().speakImmediately("今天重庆天气怎么样？")
                        speechRecorder.startRecord()
                    }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_item_margin)))

            FButton(text = "停止录音", modifier = Modifier.fillMaxWidth()) {
                speechRecorder.stopRecord()
            }

        }
    }


    /**
     * 发送意图请求
     */
    private fun sendIntent() {
        lifecycleScope.launch {
            uiView.showLoading()

            val params = HashMap<String, Any>()
            params["topic"] = "dm.input.intent"
            params["intent"] = "查天气"
            params["input"] = "查一下重庆市垫江明天的天气"
            params["task"] = "查天气"
            params["recordId"] = UUID.randomUUID().toString().replace("-", "")

            val result = withContext(Dispatchers.IO) {
                webSocketClient?.invoke(params, object : TypeReference<BasicSpeechReply>() {}) {
                    return@invoke it.recordId.startsWith(params["recordId"].toString())
                }
            }

            uiView.dismissLoading()

            if (result?.error.isNotNull()) {
                uiView.showMsgDialog(result.error?.errMsg)
            }

            Log.d("testtt", "result = $result")
        }
    }

    /**
     * 发送文本请求
     */
    private fun sendText() {
        lifecycleScope.launch {
            uiView.showLoading()

            val params = HashMap<String, Any>()
            params["topic"] = "nlu.input.text"
            params["refText"] = "重庆明天的天气是怎么样的"
            params["recordId"] = UUID.randomUUID().toString().replace("-", "")

            val result = withContext(Dispatchers.IO) {
                webSocketClient?.invoke(params, object : TypeReference<SpeechTextResult>() {}) {
                    return@invoke it.recordId.startsWith(params["recordId"].toString())
                }
            }

            uiView.dismissLoading()

            if (result?.error.isNotNull()) {
                uiView.showMsgDialog(result.error?.errMsg)
            } else if (result.isNotNull()) {
                player.play(AudioItem(result.speakUrl, AudioSourceType.PATH_URL))
            }

            Log.d("testtt", "result = $result")
        }
    }

    /**
     * 发送语音请求
     */
    private fun sendVoice() {

    }

    /**
     * 连接WebSocket
     */
    private fun connect() {
        if (webSocketClient.isNull()) {
            val params = HashMap<String, Any>()
            params["serviceType"] = "websocket"
            params["productId"] = "279635113"

            //以下参数是云对云的对接方式
            params["apikey"] = "95827aacab7e95827aacab7e6a3cecc5"

            //以下参数是设备对云的对接方式
            //params["deviceName"] = DeviceUtil.getUniqueDeviceId()
            //params["nonce"] = UUID.randomUUID().toString().replace("-", "")
            //params["sig"] = ""
            //params["timestamp"] = System.currentTimeMillis()

            webSocketClient = WebSocketClient.create()
                .url(URLUtil.completeParams("wss://dds.dui.ai/dds/v3/prod", params))
                .build()
        }

        webSocketClient?.connect()
    }

    override fun onDestroy() {
        player.stop()
        player.release()
        webSocketClient?.disconnect()
        speechRecorder.release()
        super.onDestroy()
    }
}