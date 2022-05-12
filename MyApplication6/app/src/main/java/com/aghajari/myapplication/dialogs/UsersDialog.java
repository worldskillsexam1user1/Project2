package com.aghajari.myapplication.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.myapplication.R;
import com.aghajari.myapplication.api.Model2;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class UsersDialog extends Dialog {

    List<Model2> users;
    HashSet<Model2> selectedUsers = new HashSet<>();
    OnApply a;

    public UsersDialog(@NonNull Context context, List<Model2> users, HashSet<Model2> oldModels, OnApply apply) {
        super(context);
        this.users = users;
        this.a = apply;

        if (oldModels != null)
            selectedUsers.addAll(oldModels);

        setContentView(R.layout.users);

        getWindow().getDecorView().setBackground(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerView rv = findViewById(R.id.rv);
        Adapter ad;
        rv.setAdapter(ad = new Adapter());

        findViewById(R.id.selectAll).setOnClickListener(v -> {
            selectedUsers.addAll(users);
            ad.notifyDataSetChanged();
        });
        findViewById(R.id.done).setOnClickListener(v -> {
            a.apply(selectedUsers);
            cancel();
        });

        ((EditText) findViewById(R.id.edt)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s == null)
                    return;

                if (s.length() <= 1) {
                    ad.myUsers = users;
                    ad.notifyDataSetChanged();
                    return;
                }
                List<Model2> my = new ArrayList<>();
                for (Model2 m : users) {
                    if (m.name.toLowerCase().contains(s.toString().toLowerCase()))
                        my.add(m);
                }
                ad.myUsers = my;
                ad.notifyDataSetChanged();
            }
        });
    }

    private class Adapter extends RecyclerView.Adapter<VH>{
        List<Model2> myUsers = users;

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.users_rv, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            holder.bind(myUsers.get(position), this);
        }

        @Override
        public int getItemCount() {
            return myUsers.size();
        }
    }

    public interface OnApply {
        void apply(HashSet<Model2> models);
    }

    private class VH extends RecyclerView.ViewHolder {

        AppCompatImageView img;
        AppCompatImageView selected;
        AppCompatImageView selected2;
        TextView tv;

        public VH(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
            selected = itemView.findViewById(R.id.selected);
            selected2 = itemView.findViewById(R.id.selected2);
            tv = itemView.findViewById(R.id.tv);
        }

        public void bind(Model2 model2, Adapter adapter) {
            Glide.with(img).load(model2.photo).transform(new RoundedCorners((int) (getContext().getResources().getDisplayMetrics().density * 12))).into(img);
            tv.setText(model2.name);

            boolean s = selectedUsers.contains(model2);
            tv.setTextColor(s ? 0xFF11C870 : 0xFF048ABF);
            selected.setVisibility(s ? View.VISIBLE : View.GONE);
            selected2.setVisibility(s ? View.VISIBLE : View.GONE);

            itemView.setOnClickListener(v -> {
                if (!selectedUsers.contains(model2))
                    selectedUsers.add(model2);
                else
                    selectedUsers.remove(model2);
                adapter.notifyDataSetChanged();
            });
        }
    }
}
