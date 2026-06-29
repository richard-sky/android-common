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
import com.richard.dev.common.activity.speech.model.DeviceActive
import com.richard.dev.common.activity.speech.model.SpeechResult
import com.richard.ilbrary.compose.widget.FButton
import com.richard.ilbrary.compose.widget.PageScaffold
import com.richard.library.basic.basic.BasicActivity
import com.richard.library.context.AppContext
import com.richard.library.context.util.AppUtil
import com.richard.library.context.util.DeviceUtil
import com.richard.library.context.util.IdGenerator
import com.richard.library.context.util.LogUtil
import com.richard.library.context.util.SPUtil
import com.richard.library.context.util.URLUtil
import com.richard.library.context.util.isNotNull
import com.richard.library.context.util.isNull
import com.richard.library.context.util.media.AudioItem
import com.richard.library.context.util.media.AudioSourceType
import com.richard.library.context.util.media.MediaPlayerUtil
import com.richard.library.context.util.media.TTSSpeaker
import com.richard.library.net.http.model.RequestParams
import com.richard.library.net.http.request.Requester
import com.richard.library.net.websocket.OnReceive
import com.richard.library.net.websocket.WebSocketClient
import com.richard.library.permission.PermissionRequester
import com.richard.library.security.EncryptUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.ByteString
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
            .silenceThreshDb(-110.0)
            .saveWavEnable(true)
            .build()

        speechRecorder.setCallback(object : RecorderCallback {
            override fun onVoiceStateChange(hasVoice: Boolean, currentDB: Double) {
                // UI更新分贝、人声状态
                Log.d("VAD", "是否有人声：$hasVoice DB：$currentDB")
            }

            override fun onRealTimePcm(pcmBytes: ByteArray) {
                // 实时原始PCM流，可用于实时语音识别、传输
                webSocketClient?.sendMessage(pcmBytes)
            }

            override fun onSilenceAutoStop() {
                Log.d("VAD", "静音自动结束录音")
                webSocketClient?.sendMessage(ByteString.of())
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
                        TTSSpeaker.getInstance().speakImmediately("What's the weather like in Chongqing tomorrow?")
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
            params["recordId"] = IdGenerator.generateUUID()

            val result = withContext(Dispatchers.IO) {
                webSocketClient?.invoke(params, object : TypeReference<BasicSpeechReply>() {}) {
                    return@invoke it.isRecordId(params["recordId"])
                }
            }

            uiView.dismissLoading()

            if (result.isNull()) {
                uiView.showMsgDialog("请求失败")
                return@launch
            }

            if (result.isError) {
                uiView.showMsgDialog(result.errMsg)
            }
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
            params["recordId"] = IdGenerator.generateUUID()

            val result = withContext(Dispatchers.IO) {
                webSocketClient?.invoke(params, object : TypeReference<SpeechResult>() {}) {
                    return@invoke it.isRecordId(params["recordId"])
                }
            }

            uiView.dismissLoading()

            if (result.isNull()) {
                uiView.showMsgDialog("请求失败")
                return@launch
            }

            if (result.isError) {
                uiView.showMsgDialog(result.errMsg)
            } else if (result.isNotNull()) {
                player.play(AudioItem(result.speakUrl, AudioSourceType.PATH_URL))
            }
        }
    }

    /**
     * 发送语音请求
     */
    private fun sendVoice() {
        val params = RequestParams()
        params["topic"] = "recorder.stream.start"
        params["recordId"] = IdGenerator.generateUUID()

        val audio = RequestParams()
        audio["audioType"] = "wav"
        audio["sampleRate"] = 16000
        audio["channel"] = 1
        audio["sampleBytes"] = 2

        val asrParams = RequestParams()
        asrParams["enableVAD"] = true
        //asrParams["enableCloudVAD"] = true
        asrParams["returnVadStart"] = true
        asrParams["returnVadEnd"] = true
        asrParams["res"] = "aienglish-mix"//切换到识别中英文的能力

        params["audio"] = audio
        params["asrParams"] = asrParams

        webSocketClient?.onMain(params, object : OnReceive<SpeechResult>() {

            override fun filter(data: SpeechResult): Boolean {
                return data.isRecordId(params["recordId"])
            }

            override fun onReceive(data: SpeechResult) {
                if (data.isTopic("dm.output")) {
                    player.play(AudioItem(data.speakUrl, AudioSourceType.PATH_URL))
                    uiView.showMsgDialog("你说话的内容是: ${data.dm.input.lowercase()}")
                }
            }
        })
    }


    /**
     * 连接WebSocket
     */
    private fun connect() {
        val productId = "279635113"
        val productKey = "cc5abaa3c5782ac9dd6e53dd55ed0796"
        val productSecret = "bac74618ed35f0ee122d4e8166fd3a35"
        val apiKey = "95827aacab7e95827aacab7e6a3cecc5"

        //固定本地设备id，以免平台免费额度用完
        val deviceId = "2332ca9e531fc34afba980930583bb6a2"

        lifecycleScope.launch {
            var deviceActive =
                SPUtil.getObject<DeviceActive>("deviceActive", DeviceActive::class.java)
            //设备激活
            if (deviceActive.isNull()) {
                val queryParams = RequestParams()
                queryParams["productKey"] = productKey
                queryParams["format"] = "plain"
                queryParams["productId"] = productId
                queryParams["timestamp"] = System.currentTimeMillis()
                queryParams["nonce"] = IdGenerator.getMixId(8)
                queryParams["sig"] = EncryptUtil.encryptHmacSHA1ToString(
                    "${queryParams["productKey"]}${queryParams["format"]}${queryParams["nonce"]}${queryParams["productId"]}${queryParams["timestamp"]}",
                    productSecret
                ).lowercase()

                val bodyParams = RequestParams()
                bodyParams["platform"] = "android"
                bodyParams["deviceId"] = deviceId
                bodyParams["packageName"] = AppUtil.getAppPackageName()
                bodyParams["buildVariant"] = /*BuildConfig.BUILD_TYPE*/"release"
                bodyParams["displayMatrix"] =
                    "${AppContext.getScreenWidth()}*${AppContext.getScreenHeight()}"
                bodyParams["buildModel"] = DeviceUtil.getModel()
                bodyParams["buildManufacture"] = DeviceUtil.getManufacturer()
                bodyParams["buildDevice"] = DeviceUtil.getVendor()

                val response = withContext(Dispatchers.IO) {
                    return@withContext Requester.create()
                        .url(
                            URLUtil.completeParams(
                                "https://auth.dui.ai/auth/device/register",
                                queryParams
                            )
                        )
                        .postJson(bodyParams)
                        .request(object : TypeReference<DeviceActive>() {})
                }

                if (response.errId.isNotNull()) {
                    uiView.showErrorMsg(response.error)
                    return@launch
                } else {
                    deviceActive = response
                    SPUtil.put("deviceActive", deviceActive)
                }
            }

            //连接WebSocket
            if (webSocketClient.isNull()) {
                val params = HashMap<String, Any>()
                params["serviceType"] = "websocket"
                params["productId"] = productId

                //以下参数是云对云的对接方式
                //params["apikey"] = apiKey

                //以下参数是设备对云的对接方式
                params["deviceName"] = deviceActive.deviceName
                params["nonce"] = IdGenerator.getMixId(8)
                params["timestamp"] = System.currentTimeMillis()
                params["authType"] = "DEVICESIG"
                //params["communicationType"] = "fullDuplex"//全双工传入
                params["sig"] = EncryptUtil.encryptHmacSHA1ToString(
                    "${params["deviceName"]}${params["nonce"]}${params["productId"]}${params["timestamp"]}",
                    deviceActive.deviceSecret
                ).lowercase()

                LogUtil.d("testtt", URLUtil.completeParams("wss://dds.dui.ai/dds/v3/prod", params))

                webSocketClient = WebSocketClient.create()
                    .url(URLUtil.completeParams("wss://dds.dui.ai/dds/v3/prod", params))
                    .build()
            }

            webSocketClient?.connect()
        }
    }

    override fun onDestroy() {
        player.stop()
        player.release()
        webSocketClient?.disconnect()
        speechRecorder.release()
        super.onDestroy()
    }
}