package com.aghajari.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.util.Pair;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aghajari.myapplication.api.ApiService;
import com.aghajari.myapplication.api.Model1;
import com.aghajari.myapplication.api.Model2;
import com.aghajari.myapplication.dialogs.FieldDialog;
import com.aghajari.myapplication.dialogs.UsersDialog;
import com.aghajari.myapplication.room.DBViewModel;
import com.aghajari.myapplication.room.Table;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.google.android.material.datepicker.MaterialDatePicker;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DBViewModel dbViewModel;
    private long lastAnimDuration = 500;
    private boolean clickable = false;
    private Model1 model = null;

    private HashSet<Model2> usersList, usersList2;
    private List<Table> recordList = new ArrayList<>();
    private Model2 field, field2, field3;
    private boolean timerStarted = false;
    private Handler handler = new Handler();

    private Adapter adapter1;
    private Adapter2 adapter2;

    private int year, month, day = -1;
    private long time, time1, time2;

    private int state = 0;

    TextView h, m, s;
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int sec = Integer.parseInt(s.getText().toString());
            int min = Integer.parseInt(m.getText().toString());
            int hr = Integer.parseInt(h.getText().toString());

            sec += 5;
            if (sec >= 100) {
                sec = 0;
                min++;
            }

            if (min == 60) {
                min = 0;
                hr++;
            }
            NumberFormat nf = NumberFormat.getIntegerInstance();
            nf.setMinimumIntegerDigits(2);
            h.setText(nf.format(hr));
            m.setText(nf.format(min));
            s.setText(nf.format(sec));
            handler.postDelayed(this, 10);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);
        h = findViewById(R.id.hour);
        m = findViewById(R.id.minute);
        s = findViewById(R.id.sec);

        dbViewModel = new ViewModelProvider(this).get(DBViewModel.class);

        ApiService.getData(new ApiService.Callback() {
            @Override
            public void onResponse(String json) {
                model = Model1.generate(json);
            }

            @Override
            public void onError(boolean fromNetwork, int code) {
                super.onError(fromNetwork, code);
                ApiService.getData(this);
            }
        });
        animateBackground();
        animate();

        TextView users = findViewById(R.id.users);
        users.setOnClickListener(v -> {
            if (clickable && model != null) {
                new UsersDialog(v.getContext(), model.users, usersList, (m) -> {
                    usersList = m;
                    users.setText(m.size() > 0 ? "Selected " + m.size() + " users" : "Select users ...");
                    if (m.size() > 0 ) {
                        users.setTextColor(0xFF0367A5);
                        users.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                }).show();

            } else {
                Toast.makeText(v.getContext(), "لطفا صبر کنید!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.field).setOnClickListener(v -> {
            if (clickable && model != null) {
                new FieldDialog(v.getContext(), model.fields, (m) -> {
                    field = m;
                    ((TextView) findViewById(R.id.field)).setText("");
                    ((TextView) findViewById(R.id.field2)).setText(m.name);
                    Glide.with(v).load(m.icon).into((ImageView) findViewById(R.id.img));
                }).show();

            } else {
                Toast.makeText(v.getContext(), "لطفا صبر کنید!", Toast.LENGTH_SHORT).show();
            }
        });

        TextView date = findViewById(R.id.date);
        date.setOnClickListener(v -> {
            if (clickable) {
                DatePickerDialog dialog = new DatePickerDialog(v.getContext());

                dialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
                    day = dayOfMonth;
                    MainActivity.this.year = year;
                    MainActivity.this.month = month;

                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, month, dayOfMonth);
                        time = calendar.getTime().getTime();


                    date.setTextColor(0xFF0367A5);
                    date.setTypeface(Typeface.DEFAULT_BOLD);
                    NumberFormat nf = NumberFormat.getIntegerInstance();
                    nf.setMinimumIntegerDigits(2);
                    date.setText(year + " / " + nf.format(month) + " / " + nf.format(dayOfMonth));
                });
                dialog.show();
            }
        });

        findViewById(R.id.start).setOnClickListener(v -> {
            if (field != null && usersList != null && usersList.size() > 0 && day != -1) {
                ObjectAnimator animator;
                animator = ObjectAnimator.ofFloat(findViewById(R.id.options), "alpha", 1, 0);
                animator.setDuration(800);
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        findViewById(R.id.options).setVisibility(View.GONE);
                    }
                });
                animator.start();

                animator = ObjectAnimator.ofFloat(findViewById(R.id.back), "alpha", 0, 1);
                animator.setDuration(800);
                animator.start();

                View main = findViewById(R.id.main);
                main.setVisibility(View.VISIBLE);
                animator = ObjectAnimator.ofFloat(main, "alpha", 0, 1);
                animator.setDuration(800);
                animator.start();

                Glide.with(v).load(field.icon).into((ImageView) findViewById(R.id.field_img));
                ((TextView) findViewById(R.id.field_tv)).setText(field.name);
                recordList.clear();
                ((RecyclerView) findViewById(R.id.rv_users)).setAdapter(adapter1 = new Adapter(usersList));
                ((RecyclerView) findViewById(R.id.rv_users_timer)).setAdapter(adapter2 = new Adapter2());

                // 56 + 12 + 195
                lastAnimDuration = 0;
                resize(findViewById(R.id.ws), getResources().getDisplayMetrics().widthPixels / 2 - dp(263) / 2, dp(27), dp(56), dp(56));
                resize(findViewById(R.id.ws2), getResources().getDisplayMetrics().widthPixels / 2 - dp(263) / 2 + dp(68), dp(37), dp(195), dp(47));
            }
        });

        findViewById(R.id.back).setOnClickListener(v -> onBackPressed());

        findViewById(R.id.ref).setOnClickListener(v-> {
            m.setText("00");
            s.setText("00");
            h.setText("00");
            timerStarted = false;
            findViewById(R.id.start_stop).setBackgroundResource(R.drawable.start_timer);
            handler.removeCallbacks(runnable);
            recordList.clear();
            adapter1.users.clear();
            adapter1.users.addAll(usersList);
            adapter1.notifyDataSetChanged();
            adapter2.notifyDataSetChanged();
        });

        View v = findViewById(R.id.start_stop);
        v.setOnClickListener(v0 -> {
            if (timerStarted) {
                v.setBackgroundResource(R.drawable.start_timer);
                timerStarted = false;
                handler.removeCallbacks(runnable);
            } else {
                v.setBackgroundResource(R.drawable.stop);
                timerStarted = true;
                handler.post(runnable);
            }
        });

        initTabs();
    }

    @Override
    public void onBackPressed() {
        if (state == 0)
            super.onBackPressed();
        else if (state == 1) {
            state = 0;

            ObjectAnimator animator;
            animator = ObjectAnimator.ofFloat(findViewById(R.id.main), "alpha", 0, 1);
            animator.setDuration(800);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    findViewById(R.id.main).setVisibility(View.VISIBLE);
                }
            });
            animator.start();

            animator = ObjectAnimator.ofFloat(findViewById(R.id.report_page), "alpha", 1, 0);
            animator.setDuration(800);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    findViewById(R.id.report_page).setVisibility(View.GONE);
                }
            });
            animator.start();

            animator = ObjectAnimator.ofFloat(findViewById(R.id.report_bg), "alpha", 0, 1);
            animator.setDuration(800);
            animator.start();
            animator = ObjectAnimator.ofFloat(findViewById(R.id.report_ic), "alpha", 0, 1);
            animator.setDuration(800);
            animator.start();
        }
    }

    private void animateBackground(){
        View view = findViewById(R.id.bg2);
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
        animator.setStartDelay(lastAnimDuration);
        animator.setDuration(lastAnimDuration += 1000);
        animator.start();
    }

    private void animate(){
        View view = findViewById(R.id.frame);
        ObjectAnimator animator;
        animator = ObjectAnimator.ofFloat(view, "alpha", 0, 1);
        animator.setStartDelay(lastAnimDuration);
        animator.setDuration(800);
        animator.start();
        animator = ObjectAnimator.ofFloat(view, "scaleX", 0, 1);
        animator.setStartDelay(lastAnimDuration);
        animator.setDuration(800);
        animator.start();
        animator = ObjectAnimator.ofFloat(view, "scaleY", 0, 1);
        animator.setStartDelay(lastAnimDuration);
        animator.setDuration(800);

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                lastAnimDuration = 500;
                animateTexts();
            }
        });
        animator.start();

        //lastAnimDuration += animator.getDuration() + 500;
    }

    private void animateTexts(){
        ObjectAnimator animator = ObjectAnimator.ofFloat(findViewById(R.id.year), "alpha", 1, 0);
        animator.setStartDelay(lastAnimDuration);
        animator.setDuration(800);
        animator.start();

        resize(findViewById(R.id.ws), dp(64), dp(27), dp(56), dp(56));
        resize(findViewById(R.id.ws2), dp(132), dp(37), dp(195), dp(47));

        TextView kerman = findViewById(R.id.kerman);
        resize(kerman, getResources().getDisplayMetrics().widthPixels / 2 - dp(41),
                getResources().getDisplayMetrics().heightPixels - dp(90), dp(82), dp(24));
        ObjectAnimator color = ObjectAnimator.ofArgb(kerman, "textColor", 0xFF1783A6, Color.argb(255/2, 255, 255, 255));
        color.setStartDelay(lastAnimDuration);
        color.setDuration(800);
        color.start();

        ObjectAnimator textSize = ObjectAnimator.ofFloat(kerman, "textSize", 40, 18);
        textSize.setStartDelay(lastAnimDuration);
        textSize.setDuration(800);
        textSize.start();

        lastAnimDuration += 800;
        animateReport();
    }

    private void resize(View view, int left, int top, int w, int h) {
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) view.getLayoutParams();
        int initLeft = lp.leftMargin, initTop = lp.topMargin, initW = lp.width, initH = lp.height;
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setStartDelay(lastAnimDuration);
        animator.setDuration(800);
        animator.addUpdateListener(a -> {
            float f = (float) a.getAnimatedValue();
            lp.leftMargin = (int) ((left - initLeft) * f + initLeft);
            lp.topMargin = (int) ((top - initTop) * f + initTop);
            if (w != -2)
                lp.width = (int) ((w - initW) * f + initW);
            if (h != -2)
                lp.height = (int) ((h - initH) * f + initH);
            view.setLayoutParams(lp);
        });
        animator.start();
    }

    private int dp(int v) {
        return (int) (getResources().getDisplayMetrics().density * v);
    }

    private void animateReport(){
        ObjectAnimator animator;
        animator = ObjectAnimator.ofFloat(findViewById(R.id.report_bg), "alpha", 0, 1);
        animator.setStartDelay(lastAnimDuration);
        animator.setDuration(800);
        animator.start();

        animator = ObjectAnimator.ofFloat(findViewById(R.id.report_ic), "alpha", 0, 1);
        animator.setStartDelay(lastAnimDuration);
        animator.setDuration(800);
        animator.start();

        animator = ObjectAnimator.ofFloat(findViewById(R.id.options), "alpha", 0, 1);
        animator.setStartDelay(lastAnimDuration);
        animator.setDuration(800);
        animator.start();
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                clickable = true;
            }
        });
    }

    private class Adapter extends RecyclerView.Adapter<VH>{
        private final List<Model2> users;

        private Adapter(HashSet<Model2> users) {
            this.users = new ArrayList<>(users);
        }


        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.users_main_rv, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            holder.bind(users.get(position), this);
        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    }

    private class VH extends RecyclerView.ViewHolder {

        AppCompatImageView img;
        TextView tv;

        public VH(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
        }

        public void bind(Model2 model2, Adapter adapter) {
            Glide.with(img).load(model2.photo).transform(new RoundedCorners((int) (itemView.getContext().getResources().getDisplayMetrics().density * 12))).into(img);

            itemView.setOnClickListener(v ->  {
                if (timerStarted) {
                    Table table = new Table(model2.name, model2.photo, field.id, field.name, Integer.parseInt(h.getText().toString()),
                            Integer.parseInt(m.getText().toString()), Integer.parseInt(s.getText().toString()), year, month, day, time);
                    recordList.add(table);
                    dbViewModel.insert(table);
                    adapter2.notifyDataSetChanged();
                    adapter.users.remove(model2);
                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    private class Adapter2 extends RecyclerView.Adapter<VH2>{

        @NonNull
        @Override
        public VH2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH2(LayoutInflater.from(parent.getContext()).inflate(R.layout.users_rec_rv, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH2 holder, int position) {
            holder.bind(recordList.get(position));
        }

        @Override
        public int getItemCount() {
            return recordList.size();
        }
    }

    private class VH2 extends RecyclerView.ViewHolder {

        AppCompatImageView img;
        TextView tv, tv2;

        public VH2(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
            tv = itemView.findViewById(R.id.tv);
            tv2 = itemView.findViewById(R.id.tv2);
        }

        public void bind(Table table) {
            Glide.with(img).load(table.getPhoto()).transform(new RoundedCorners((int) (itemView.getContext().getResources().getDisplayMetrics().density * 12))).into(img);
            NumberFormat nf = NumberFormat.getIntegerInstance();
            nf.setMinimumIntegerDigits(2);
            tv.setText(nf.format(table.getH()) + " : " + nf.format(table.getM()) + " : " + nf.format(table.getS()));
            tv2.setText(table.getName());

        }
    }

    private void initTabs() {

        findViewById(R.id.report_bg).setOnClickListener(v -> {
            if (!clickable || state != 0)
                return;

            state = 1;

            ObjectAnimator animator;
            animator = ObjectAnimator.ofFloat(findViewById(R.id.main), "alpha", 1, 0);
            animator.setDuration(800);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    findViewById(R.id.main).setVisibility(View.GONE);
                }
            });
            animator.start();

            animator = ObjectAnimator.ofFloat(findViewById(R.id.report_bg), "alpha", 1, 0);
            animator.setDuration(800);
            animator.start();
            animator = ObjectAnimator.ofFloat(findViewById(R.id.report_ic), "alpha", 1, 0);
            animator.setDuration(800);
            animator.start();

            animator = ObjectAnimator.ofFloat(findViewById(R.id.report_page), "alpha", 0, 1);
            animator.setDuration(800);
            animator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationStart(Animator animation) {
                    findViewById(R.id.report_page).setVisibility(View.VISIBLE);
                }
            });
            animator.start();
        });

        findViewById(R.id.tab1_c).setOnClickListener(v -> {
            findViewById(R.id.tab1).setVisibility(View.VISIBLE);
            findViewById(R.id.tab2).setVisibility(View.GONE);

            findViewById(R.id.tab1_content).setVisibility(View.VISIBLE);
            findViewById(R.id.tab2_content).setVisibility(View.GONE);
        });
        findViewById(R.id.tab2_c).setOnClickListener(v -> {
            findViewById(R.id.tab2).setVisibility(View.VISIBLE);
            findViewById(R.id.tab1).setVisibility(View.GONE);

            findViewById(R.id.tab2_content).setVisibility(View.VISIBLE);
            findViewById(R.id.tab1_content).setVisibility(View.GONE);
        });

        TextView users = findViewById(R.id.users2);
        users.setOnClickListener(v -> {
            if (clickable && model != null) {
                new UsersDialog(v.getContext(), model.users, usersList2, (m) -> {
                    usersList2 = m;
                    users.setText(m.size() > 0 ? "Selected " + m.size() + " users" : "Select users ...");
                    if (m.size() > 0 ) {
                        users.setTextColor(0xFF0367A5);
                        users.setTypeface(Typeface.DEFAULT_BOLD);
                    }
                }).show();

            } else {
                Toast.makeText(v.getContext(), "لطفا صبر کنید!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.field3).setOnClickListener(v -> {
            if (clickable && model != null) {
                new FieldDialog(v.getContext(), model.fields, (m) -> {
                    field2 = m;
                    ((TextView) findViewById(R.id.field3)).setText("");
                    ((TextView) findViewById(R.id.field4)).setText(m.name);
                    Glide.with(v).load(m.icon).into((ImageView) findViewById(R.id.img2));
                }).show();

            } else {
                Toast.makeText(v.getContext(), "لطفا صبر کنید!", Toast.LENGTH_SHORT).show();
            }
        });

        TextView date = findViewById(R.id.date2);
        date.setOnClickListener(v -> {
            if (clickable) {
                MaterialDatePicker<Pair<Long, Long>> d = MaterialDatePicker.Builder.dateRangePicker().build();
                d.addOnPositiveButtonClickListener(selection -> {
                    time1 = selection.first;
                    time2 = selection.second;
                    Calendar d1 = Calendar.getInstance();
                    d1.setTimeInMillis(selection.first);
                    Calendar d2 = Calendar.getInstance();
                    d2.setTimeInMillis(selection.second);

                    date.setTextColor(0xFF0367A5);
                    date.setTypeface(Typeface.DEFAULT_BOLD);
                    NumberFormat nf = NumberFormat.getIntegerInstance();
                    nf.setMinimumIntegerDigits(2);
                    date.setText(d1.get(Calendar.YEAR) + " / " + nf.format(d1.get(Calendar.MONTH)) + " / " + nf.format(d1.get(Calendar.DAY_OF_MONTH)) + " - " +
                            d2.get(Calendar.YEAR) + " / " + nf.format(d2.get(Calendar.MONTH)) + " / " + nf.format(d2.get(Calendar.DAY_OF_MONTH)));
                });
                d.show(getSupportFragmentManager(), null);
            }
        });

        findViewById(R.id.field5).setOnClickListener(v -> {
            if (clickable && model != null) {
                new FieldDialog(v.getContext(), model.fields, (m) -> {
                    field3 = m;
                    ((TextView) findViewById(R.id.field5)).setText("");
                    ((TextView) findViewById(R.id.field6)).setText(m.name);
                    Glide.with(v).load(m.icon).into((ImageView) findViewById(R.id.img3));
                }).show();

            } else {
                Toast.makeText(v.getContext(), "لطفا صبر کنید!", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.show2).setOnClickListener(v -> {
            if (field3 != null) {
                ((RecyclerView) findViewById(R.id.rv_tab2)).setAdapter(new Adapter3(field3.id));
                findViewById(R.id.options_tab2).setVisibility(View.GONE);
                findViewById(R.id.result_tab2).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.back_tab2).setOnClickListener(v -> {
            findViewById(R.id.options_tab2).setVisibility(View.VISIBLE);
            findViewById(R.id.result_tab2).setVisibility(View.GONE);
        });

        findViewById(R.id.show).setOnClickListener(v -> {
            if (field2 != null && time1 != 0 && usersList2 != null && usersList2.size() > 0) {
                findViewById(R.id.options_tab1).setVisibility(View.GONE);
                findViewById(R.id.result_tab1).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.back_tab1).setOnClickListener(v -> {
            findViewById(R.id.options_tab1).setVisibility(View.VISIBLE);
            findViewById(R.id.result_tab1).setVisibility(View.GONE);
        });
    }

    private class Adapter3 extends RecyclerView.Adapter<VH3>{

        List<Table> tables = new ArrayList<>();
        private Adapter3(int id) {
           dbViewModel.getListTab2(id).observe(MainActivity.this, t -> {
               tables.addAll(t);
               notifyDataSetChanged();
           });
        }

        @NonNull
        @Override
        public VH3 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH3(LayoutInflater.from(parent.getContext()).inflate(R.layout.tab2_rv, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH3 holder, int position) {
            holder.bind(tables.get(position), position + 1);
        }

        @Override
        public int getItemCount() {
            return tables.size();
        }
    }

    private class VH3 extends RecyclerView.ViewHolder {

        AppCompatImageView img;
        TextView index, name, duration, date;

        public VH3(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
            duration = itemView.findViewById(R.id.duration);
            date = itemView.findViewById(R.id.date);
            index = itemView.findViewById(R.id.index);
        }

        public void bind(Table table, int i) {
            Glide.with(img).load(table.getPhoto()).transform(new RoundedCorners((int) (itemView.getContext().getResources().getDisplayMetrics().density * 12))).into(img);
            NumberFormat nf = NumberFormat.getIntegerInstance();
            nf.setMinimumIntegerDigits(2);
            duration.setText(nf.format(table.getH()) + " : " + nf.format(table.getM()) + " : " + nf.format(table.getS()));
            date.setText(table.getYear() + " / " + nf.format(table.getMonth()) + " / " + nf.format(table.getDay()));
            name.setText(table.getName());
            index.setText(i + ".");

            if (i == 1) {
                index.setTextColor(0xFFFFE500);
                duration.setTextColor(0xFFFFE500);
                name.setTextColor(0xFFFFE500);
                date.setTextColor(0xFFFFE500);
            } else if (i == 2) {
                index.setTextColor(0xFFCFEDEB);
                duration.setTextColor(0xFFCFEDEB);
                name.setTextColor(0xFFCFEDEB);
                date.setTextColor(0xFFCFEDEB);
            } else if (i == 3) {
                index.setTextColor(0xFFEB9128);
                duration.setTextColor(0xFFEB9128);
                name.setTextColor(0xFFEB9128);
                date.setTextColor(0xFFEB9128);
            } else {
                index.setTextColor(0xFF8BE3DE);
                duration.setTextColor(0xFF8BE3DE);
                name.setTextColor(0xFF8BE3DE);
                date.setTextColor(0xFF8BE3DE);
            }
        }
    }
}