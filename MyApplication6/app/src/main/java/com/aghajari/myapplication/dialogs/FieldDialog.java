package com.aghajari.myapplication.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.aghajari.myapplication.R;
import com.aghajari.myapplication.api.Model2;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class FieldDialog extends Dialog {

    List<Model2> fields;
    OnApply a;

    public FieldDialog(@NonNull Context context, List<Model2> fields, OnApply apply) {
        super(context);
        this.fields = fields;
        this.a = apply;

        setContentView(R.layout.field);

        getWindow().getDecorView().setBackground(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerView rv = findViewById(R.id.rv);
        rv.setAdapter(new Adapter());
    }

    private class Adapter extends RecyclerView.Adapter<VH>{

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.field_rv, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            holder.bind(fields.get(position));
        }

        @Override
        public int getItemCount() {
            return fields.size();
        }
    }

    public interface OnApply {
        void apply(Model2 model2);
    }

    private class VH extends RecyclerView.ViewHolder {

        AppCompatImageView img;
        TextView tv;

        public VH(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.img);
            tv = itemView.findViewById(R.id.tv);
        }

        public void bind(Model2 model2) {
            Glide.with(img).load(model2.icon).into(img);
            tv.setText(model2.name);

            itemView.setOnClickListener(v ->  {
                a.apply(model2);
                cancel();
            });
        }
    }
}
