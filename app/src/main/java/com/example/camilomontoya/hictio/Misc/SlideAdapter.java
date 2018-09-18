package com.example.camilomontoya.hictio.Misc;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.camilomontoya.hictio.R;

public class SlideAdapter extends PagerAdapter {

    Context c;
    LayoutInflater inflater;

    //Lista de titulos
    private String[] titles = {
            "NAVEGAR",
            "ESTANQUE",
            "OPCIONES"
    };

    public SlideAdapter(Context c) {
        this.c = c;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int i) {
        inflater = (LayoutInflater) c.getSystemService(c.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.menu_slide, container, false);
        TextView layoutTitle = (TextView) v.findViewById(R.id.menu_title);

        layoutTitle.setTypeface(Typo.getInstance().getTitle());
        layoutTitle.setText(titles[i]);

        container.addView(v);

        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View)object);
    }
}
