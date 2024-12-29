package com.homocodian.chargeguard

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.homocodian.chargeguard.broadcast.NotificationDismissedReceiver
import com.homocodian.chargeguard.ui.theme.ChargeGuardTheme

class ChargingLimitReached : ComponentActivity() {

  private var finishReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
      finish()
    }
  }

  @SuppressLint("UnspecifiedRegisterReceiverFlag")
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge()
    showWhenLockedAndTurnScreenOn()

    val filter = IntentFilter()
    filter.addAction(this.packageName + ".FINISH_ACTIVITY")

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      registerReceiver(finishReceiver, filter, RECEIVER_EXPORTED)
    } else {
      registerReceiver(finishReceiver, filter)
    }

    setContent {
      ChargeGuardTheme {
        Surface {
          FullScreenAlert()
        }
      }
    }

    hideNavbar()
  }

  override fun onDestroy() {
    super.onDestroy()
    Log.d(TAG, "onDestroy: Charging limit reached")
    unregisterReceiver(finishReceiver)
  }

  override fun onWindowFocusChanged(hasFocus: Boolean) {
    super.onWindowFocusChanged(hasFocus)
    if (hasFocus) {
      hideNavbar()
    }
  }

  private fun showWhenLockedAndTurnScreenOn() {
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O_MR1) {
      Log.d("ActivityDebug", "Using setShowWhenLocked and setTurnScreenOn")
      setShowWhenLocked(true)
      setTurnScreenOn(true)
    } else {
      Log.d("ActivityDebug", "Using Window Flags")
      @Suppress("DEPRECATION")
      window.addFlags(
        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
          or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
      )
    }
  }

  private fun hideNavbar() {
    WindowCompat.getInsetsController(window, window.decorView).apply {
      hide(WindowInsetsCompat.Type.navigationBars())
    }
  }
}

@Composable
fun FullScreenAlert() {
  val context = LocalContext.current

  var dismissed by remember {
    mutableStateOf(false)
  }

  Column(
    modifier = Modifier
      .fillMaxSize(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Text(text = "Charging Limit Reached!", fontSize = 18.sp)
    Button(
      onClick = {
        dismissed = true
        // Send a broadcast to stop the media player or service
        val stopIntent = Intent(context, NotificationDismissedReceiver::class.java).apply {
          action = NotificationDismissedReceiver.Actions.STOP.toString()
        }
        context.applicationContext.sendBroadcast(stopIntent)
      },
      modifier = Modifier.padding(16.dp),
      enabled = !dismissed
    ) {
      Text(text = "Dismiss")
    }
  }

}

@Preview(
  uiMode = Configuration.UI_MODE_NIGHT_YES,
  name = "DefaultPreviewDark"
)
@Preview(
  uiMode = Configuration.UI_MODE_NIGHT_NO,
  name = "DefaultPreviewLight"
)
@Composable
fun PreviewFullScreenAlert() {
  FullScreenAlert()
}