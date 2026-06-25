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
import com.richard.library.context.util.startsWith
import com.richard.library.net.websocket.WebSocketClient
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

            FButton(text = "连接", modifier = Modifier.fillMaxWidth()) {
                connect()
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_item_margin)))

            FButton(text = "断开连接", modifier = Modifier.fillMaxWidth()) {
                webSocketClient?.disconnect()
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_item_margin)))

            FButton(text = "发送文本请求", modifier = Modifier.fillMaxWidth()) {
                sendText()
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_item_margin)))

            FButton(text = "发送意图请求", modifier = Modifier.fillMaxWidth()) {
                sendIntent()
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.content_item_margin)))

            FButton(text = "发送语音请求", modifier = Modifier.fillMaxWidth()) {
                sendVoice()
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
        webSocketClient?.disconnect()
        super.onDestroy()
    }
}