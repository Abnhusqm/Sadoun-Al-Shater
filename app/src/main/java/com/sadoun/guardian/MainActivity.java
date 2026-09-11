package com.sadoun.guardian;

import android.app.*;
import android.os.*;
import android.content.*;
import android.speech.RecognizerIntent;
import android.view.*;
import android.widget.*;
import java.util.*;

public class MainActivity extends Activity {
 TextView status,message,log; StringBuilder events=new StringBuilder();
 @Override public void onCreate(Bundle b){super.onCreate(b); setContentView(R.layout.activity_main);
  status=findViewById(R.id.status); message=findViewById(R.id.message); log=findViewById(R.id.log);
  findViewById(R.id.talk).setOnClickListener(v->talk());
  findViewById(R.id.guardian).setOnClickListener(v->guardian());
  findViewById(R.id.sos).setOnClickListener(v->sos());
  findViewById(R.id.tool).setOnClickListener(v->toolRequest());
  requestPermissions(new String[]{"android.permission.RECORD_AUDIO","android.permission.ACCESS_FINE_LOCATION"},10);
 }
 void add(String s){events.append(new java.text.SimpleDateFormat("HH:mm:ss").format(new Date())).append(" — ").append(s).append("\n"); log.setText(events.toString());}
 void talk(){try{Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ar"); i.putExtra(RecognizerIntent.EXTRA_PROMPT,"تحدث مع سعدون"); startActivityForResult(i,20); add("بدء الاستماع الصوتي");}catch(Exception e){message.setText("سعدون: خدمة التعرف الصوتي غير متاحة على هذا الجهاز.");}}
 void guardian(){status.setText("● متصل — 🛡️ الحارس مفعّل"); message.setText("سعدون: تم تشغيل وضع الحارس. هذه النسخة لا ترسل بلاغات حقيقية."); add("تشغيل وضع الحارس");}
 void sos(){new AlertDialog.Builder(this).setTitle("SOS تجريبي").setMessage("هذا اختبار فقط. لن يتم الاتصال بالطوارئ أو إرسال موقعك.").setPositiveButton("موافق",(d,w)->add("اختبار SOS بدون إرسال خارجي")).show();}
 void toolRequest(){new AlertDialog.Builder(this).setTitle("🛠️ طلب أداة").setMessage("الأداة: Research Parser\n\nالسبب: تحليل مصدر بحثي معقد.\nالصلاحيات: قراءة ملف محدد فقط.\nالمخاطر: منخفضة.\nالبديل: تحليل يدوي محدود.\n\nهل تأذن بالتجهيز؟").setPositiveButton("موافق",(d,w)->{message.setText("سعدون: تمت الموافقة. سأجهز الأداة ثم أكمل المهمة.");add("موافقة على الأداة — تجهيز تجريبي");}).setNegativeButton("رفض",(d,w)->{message.setText("سعدون: تم الرفض. سأستخدم البديل المتاح.");add("رفض الأداة — استخدام بديل");}).show();}
 @Override protected void onActivityResult(int r,int c,Intent d){super.onActivityResult(r,c,d); if(r==20&&c==RESULT_OK&&d!=null){ArrayList<String>x=d.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);if(x!=null&&!x.isEmpty()){message.setText("سمعتك: "+x.get(0));add("استقبلت أمراً صوتياً: "+x.get(0));}}}
}
