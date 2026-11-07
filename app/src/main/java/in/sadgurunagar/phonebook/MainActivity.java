package in.sadgurunagar.phonebook;

import android.app.*;
import android.os.Bundle;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.*;

public class MainActivity extends Activity {
    private final String PREF = "village_phonebook";
    private final String CONTACTS = "contacts";
    private final String PIN = "admin_pin";
    private LinearLayout root, list;
    private EditText search;
    private JSONArray contacts = new JSONArray();
    private final int green = Color.rgb(22, 105, 76);

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(green);
        getWindow().setNavigationBarColor(Color.BLACK);
        load();
        showHome();
    }

    private void load() {
        try { contacts = new JSONArray(getSharedPreferences(PREF,0).getString(CONTACTS,"[]")); }
        catch(Exception e) { contacts = new JSONArray(); }
    }
    private void save() {
        getSharedPreferences(PREF,0).edit().putString(CONTACTS,contacts.toString()).apply();
    }
    private TextView text(String s, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        if(bold) t.setTypeface(null, Typeface.BOLD);
        return t;
    }
    private LinearLayout vertical() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.VERTICAL);
        l.setPadding(18,16,18,12); return l;
    }
    private LinearLayout horizontal() {
        LinearLayout l = new LinearLayout(this); l.setOrientation(LinearLayout.HORIZONTAL);
        l.setGravity(Gravity.CENTER_VERTICAL); return l;
    }
    private GradientDrawable bg(int color, int radius) {
        GradientDrawable d = new GradientDrawable(); d.setColor(color); d.setCornerRadius(radius); return d;
    }
    private Button button(String label) {
        Button b = new Button(this); b.setText(label); b.setAllCaps(false); return b;
    }
    private void showHome() {
        root = vertical(); root.setBackgroundColor(Color.rgb(246,248,247));
        LinearLayout header = vertical(); header.setBackground(bg(green,24));
        TextView title = text("સદગુરુનગર ફોન બુક",23,Color.WHITE,true);
        header.addView(title);
        TextView sub = text("તમારા ગામના લોકોની સંપર્ક ડિરેક્ટરી",13,Color.WHITE,false);
        header.addView(sub);
        root.addView(header, new LinearLayout.LayoutParams(-1,-2));

        search = new EditText(this); search.setSingleLine(true);
        search.setHint("નામ અથવા મોબાઇલ નંબર શોધો");
        search.setCompoundDrawablePadding(8);
        search.setPadding(16,10,16,10); search.setBackground(bg(Color.WHITE,18));
        LinearLayout.LayoutParams sp = new LinearLayout.LayoutParams(-1,-2); sp.topMargin=16;
        root.addView(search,sp);

        LinearLayout actions = horizontal();
        TextView count = text("સંપર્કો",16,Color.DKGRAY,true);
        actions.addView(count,new LinearLayout.LayoutParams(0,-2,1));
        Button admin = button("Admin");
        admin.setOnClickListener(v -> adminLogin()); actions.addView(admin);
        LinearLayout.LayoutParams ap = new LinearLayout.LayoutParams(-1,-2); ap.topMargin=8;
        root.addView(actions,ap);

        ScrollView scroll = new ScrollView(this);
        list = vertical(); list.setPadding(0,8,0,8); scroll.addView(list);
        root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        search.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s,int st,int c,int a){}
            public void onTextChanged(CharSequence s,int st,int before,int count){ render(s.toString()); }
            public void afterTextChanged(Editable e){}
        });
        setContentView(root); render("");
    }
    private void render(String query) {
        list.removeAllViews();
        int shown=0;
        for(int i=0;i<contacts.length();i++) {
            JSONObject c=contacts.optJSONObject(i); if(c==null) continue;
            String name=c.optString("name"), phone=c.optString("phone"), detail=c.optString("detail");
            String q=query.toLowerCase(Locale.ROOT);
            if(!name.toLowerCase(Locale.ROOT).contains(q) && !phone.contains(q) && !detail.toLowerCase(Locale.ROOT).contains(q)) continue;
            shown++;
            LinearLayout card=vertical(); card.setBackground(bg(Color.WHITE,18));
            LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2); cp.bottomMargin=10;
            TextView n=text(name,18,Color.rgb(35,45,40),true); card.addView(n);
            if(!detail.isEmpty()) card.addView(text(detail,13,Color.GRAY,false));
            LinearLayout row=horizontal();
            TextView p=text(phone,15,Color.DKGRAY,false); row.addView(p,new LinearLayout.LayoutParams(0,-2,1));
            Button call=button("Call"); call.setTextColor(green);
            call.setOnClickListener(v -> { Intent in=new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone)); startActivity(in); });
            row.addView(call); card.addView(row);
            list.addView(card,cp);
        }
        if(shown==0) {
            TextView empty=text(query.isEmpty()?"હજુ કોઈ સંપર્ક ઉમેરાયો નથી":"કોઈ સંપર્ક મળ્યો નથી",15,Color.GRAY,false);
            empty.setGravity(Gravity.CENTER); empty.setPadding(8,40,8,40); list.addView(empty);
        }
    }
    private void adminLogin() {
        EditText pin=new EditText(this); pin.setHint("Admin password"); pin.setInputType(129);
        new AlertDialog.Builder(this).setTitle("Admin Login").setMessage("પાસવર્ડ દાખલ કરો")
            .setView(pin).setNegativeButton("Cancel",null).setPositiveButton("Login",(d,w)->{
                String saved=getSharedPreferences(PREF,0).getString(PIN,"2468");
                if(pin.getText().toString().equals(saved)) adminPanel();
                else Toast.makeText(this,"ખોટો પાસવર્ડ",Toast.LENGTH_SHORT).show();
            }).show();
    }
    private void adminPanel() {
        String[] opts={"નવો સંપર્ક ઉમેરો","Admin password બદલો"};
        new AlertDialog.Builder(this).setTitle("Admin Panel").setItems(opts,(d,which)->{
            if(which==0) contactForm(-1); else changePin();
        }).setPositiveButton("Close",null).show();
        // Long-press a contact card is not required: manage contacts from the list below.
        new AlertDialog.Builder(this).setTitle("સંપર્ક મેનેજ કરો")
            .setMessage("સંપર્કમાં ફેરફાર અથવા કાઢી નાખવા માટે નીચેની યાદી ખોલો.")
            .setPositiveButton("Open list",(d,w)->manageList()).setNegativeButton("Close",null).show();
    }
    private void manageList() {
        if(contacts.length()==0) { Toast.makeText(this,"કોઈ સંપર્ક નથી",Toast.LENGTH_SHORT).show(); return; }
        String[] names=new String[contacts.length()];
        for(int i=0;i<contacts.length();i++) names[i]=contacts.optJSONObject(i).optString("name");
        new AlertDialog.Builder(this).setTitle("સંપર્ક પસંદ કરો").setItems(names,(d,which)->{
            final int index=which;
            new AlertDialog.Builder(this).setTitle(names[index]).setItems(new String[]{"Edit","Delete"},(dd,choice)->{
                if(choice==0) contactForm(index);
                else new AlertDialog.Builder(this).setMessage("આ સંપર્ક કાઢી નાખવો છે?")
                    .setNegativeButton("ના",null).setPositiveButton("હા",(x,y)->{
                        JSONArray next=new JSONArray();
                        for(int j=0;j<contacts.length();j++) if(j!=index) next.put(contacts.optJSONObject(j));
                        contacts=next; save(); render(search.getText().toString());
                    }).show();
            }).show();
        }).show();
    }
    private void contactForm(int index) {
        JSONObject old=index>=0?contacts.optJSONObject(index):new JSONObject();
        LinearLayout form=vertical();
        EditText name=new EditText(this); name.setHint("નામ"); name.setText(old.optString("name"));
        EditText phone=new EditText(this); phone.setHint("મોબાઇલ નંબર"); phone.setInputType(3); phone.setText(old.optString("phone"));
        EditText detail=new EditText(this); detail.setHint("વિસ્તાર / વ્યવસાય (વૈકલ્પિક)"); detail.setText(old.optString("detail"));
        form.addView(name); form.addView(phone); form.addView(detail);
        new AlertDialog.Builder(this).setTitle(index<0?"નવો સંપર્ક":"સંપર્ક સંપાદિત કરો").setView(form)
            .setNegativeButton("Cancel",null).setPositiveButton("Save",(d,w)->{
                String n=name.getText().toString().trim(), p=phone.getText().toString().trim();
                if(n.isEmpty()||p.isEmpty()) { Toast.makeText(this,"નામ અને નંબર જરૂરી છે",Toast.LENGTH_SHORT).show(); return; }
                try {
                    JSONObject c=new JSONObject(); c.put("name",n); c.put("phone",p); c.put("detail",detail.getText().toString().trim());
                    if(index<0) contacts.put(c); else contacts.put(index,c);
                    save(); render(search.getText().toString());
                } catch(Exception e) { Toast.makeText(this,"Save failed",Toast.LENGTH_SHORT).show(); }
            }).show();
    }
    private void changePin() {
        EditText pin=new EditText(this); pin.setHint("New admin password"); pin.setInputType(129);
        new AlertDialog.Builder(this).setTitle("Change password").setView(pin)
            .setNegativeButton("Cancel",null).setPositiveButton("Save",(d,w)->{
                String value=pin.getText().toString();
                if(value.length()<4) Toast.makeText(this,"ઓછામાં ઓછા 4 અક્ષર રાખો",Toast.LENGTH_SHORT).show();
                else { getSharedPreferences(PREF,0).edit().putString(PIN,value).apply(); Toast.makeText(this,"Password changed",Toast.LENGTH_SHORT).show(); }
            }).show();
    }
}
