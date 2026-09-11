package com.sadoun.guardian;

import android.Manifest;
import android.app.*;
import android.os.*;
import android.content.*;
import android.net.Uri;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.*;
import java.util.*;
import java.util.Locale;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    TextView status,message,log; StringBuilder events=new StringBuilder();
    boolean guardianOn=false; TextToSpeech tts;
    BroadcastReceiver receiver=new BroadcastReceiver(){ public void onReceive(Context c,Intent i){
        String state=i.getStringExtra("state"), text=i.getStringExtra("text");
        if(state!=null){ status.setText(state.equals("ATTENTION")?"● انتباه — 🛡️ الحارس نشط":"● طبيعي — 🛡️ الحارس نشط"); message.setText("سعدون: "+text); add("الحارس: "+state+" — "+text); if(state.equals("ATTENTION")) speak("تنبيه. لاحظت نشاطاً حركياً غير معتاد. تحقق من وضعك."); }
    }};
    @Override public void onCreate(Bundle b){super.onCreate(b); setContentView(R.layout.activity_main);
        status=findViewById(R.id.status); message=findViewById(R.id.message); log=findViewById(R.id.log);
        tts=new TextToSpeech(this,this);
        findViewById(R.id.talk).setOnClickListener(v->talk());
        findViewById(R.id.guardian).setOnClickListener(v->guardian());
        findViewById(R.id.sos).setOnClickListener(v->sos());
        findViewById(R.id.tool).setOnClickListener(v->toolRequest());
        if(Build.VERSION.SDK_INT>=33) requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.POST_NOTIFICATIONS},10);
        else requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.ACCESS_FINE_LOCATION},10);
    }
    @Override protected void onStart(){super.onStart(); registerReceiver(receiver,new IntentFilter(GuardianService.ACTION_STATE), Build.VERSION.SDK_INT>=33?Context.RECEIVER_NOT_EXPORTED:0);}
    @Override protected void onStop(){try{unregisterReceiver(receiver);}catch(Exception ignored){} super.onStop();}
    @Override protected void onDestroy(){if(tts!=null){tts.stop();tts.shutdown();} super.onDestroy();}
    void add(String s){events.append(new java.text.SimpleDateFormat("HH:mm:ss",Locale.US).format(new Date())).append(" — ").append(s).append("\n"); log.setText(events.toString());}
    void talk(){try{Intent i=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); i.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"ar-YE"); i.putExtra(RecognizerIntent.EXTRA_PROMPT,"تحدث مع سعدون"); startActivityForResult(i,20); add("بدء الاستماع الصوتي");}catch(Exception e){message.setText("سعدون: خدمة التعرف الصوتي غير متاحة على هذا الجهاز.");}}
    void guardian(){
        if(!guardianOn){ Intent s=new Intent(this,GuardianService.class); if(Build.VERSION.SDK_INT>=26) startForegroundService(s); else startService(s); guardianOn=true; status.setText("● طبيعي — 🛡️ الحارس نشط"); message.setText("سعدون: الحارس يعمل الآن في الخلفية. المراقبة محلية ولا تُرسل بلاغاً تلقائياً."); add("تشغيل الحارس في الخلفية"); speak("تم تشغيل الحارس. أنا معك."); }
        else { stopService(new Intent(this,GuardianService.class)); guardianOn=false; status.setText("● متصل — الحارس متوقف"); message.setText("سعدون: تم إيقاف الحارس."); add("إيقاف الحارس"); }
    }
    void sos(){ new AlertDialog.Builder(this).setTitle("🆘 SOS").setMessage("سيتم فتح تطبيق الهاتف على شاشة الاتصال فقط. لن يتم إجراء الاتصال دون تأكيدك.").setNegativeButton("إلغاء",null).setPositiveButton("فتح الاتصال",(d,w)->{try{startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:")));add("فتح شاشة الاتصال — لا يوجد اتصال تلقائي");}catch(Exception e){add("تعذر فتح تطبيق الهاتف");}}).show(); }
    void toolRequest(){new AlertDialog.Builder(this).setTitle("🛠️ طلب أداة").setMessage("الأداة: Research Parser\n\nالسبب: تحليل مصدر بحثي معقد.\nالصلاحيات: قراءة ملف محدد فقط.\nالمخاطر: منخفضة.\n\nلن يتم تنزيل أو تثبيت أي أداة دون موافقتك.").setPositiveButton("موافق",(d,w)->{message.setText("سعدون: تمت الموافقة. سأجهز الأداة ثم أكمل المهمة.");add("موافقة على الأداة — تجهيز تجريبي");}).setNegativeButton("رفض",(d,w)->{message.setText("سعدون: تم الرفض. سأستخدم البديل المتاح.");add("رفض الأداة — استخدام بديل");}).show();}
    @Override protected void onActivityResult(int r,int c,Intent d){super.onActivityResult(r,c,d); if(r==20&&c==RESULT_OK&&d!=null){ArrayList<String>x=d.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);if(x!=null&&!x.isEmpty()){String text=x.get(0);message.setText("سمعتك: "+text);add("أمر صوتي: "+text);speak("سمعتك. سأتابع الأمر وفق الصلاحيات المسموحة.");}}}
    @Override public void onInit(int status){if(status==TextToSpeech.SUCCESS){tts.setLanguage(new Locale("ar","SA"));}}
    void speak(String text){if(tts!=null)tts.speak(text,TextToSpeech.QUEUE_FLUSH,null,"sadoun");}
}
