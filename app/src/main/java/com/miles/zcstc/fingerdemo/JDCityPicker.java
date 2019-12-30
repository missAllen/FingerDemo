package com.miles.zcstc.fingerdemo;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：Allen
 * 时间：2019/11/12 14:27
 * 描述：仿京东城市选择器
 */

public class JDCityPicker extends PopupWindow {
    View view;
    Context mContext;
    RecyclerView mRvProvince;
    RecyclerView mRvCity;
    RecyclerView mRvArea;
    ArrayList<CityInfoBean> beans;
    List<String> provinceList = new ArrayList<>();
    List<String> cityList = new ArrayList<>();
    List<String> areaList = new ArrayList<>();
    ProvinceAdapter mProvinceAdapter;
    CityAdapter mCityAdapter;
    AreaAdapter mAreaAdapter;
    String province, city, area;
    CityInfoBean mCityInfoBean;
    TextView mTvProvince;
    TextView mTvCity;
    TextView mTvArea;
    ImageView mImgClose;
    LinearLayout mLlSelect;
    onCitySelect citySelect;

    public JDCityPicker(Context context, onCitySelect citySelect) {
        super(context);
        this.mContext = context;
        this.citySelect = citySelect;
        init(context);
    }

    private void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.dialog_city_picker, null);
        mRvProvince = view.findViewById(R.id.rv_province);
        mRvCity = view.findViewById(R.id.rv_city);
        mRvArea = view.findViewById(R.id.rv_area);
        mTvProvince = view.findViewById(R.id.tv_province);
        mTvCity = view.findViewById(R.id.tv_city);
        mTvArea = view.findViewById(R.id.tv_area);
        mLlSelect = view.findViewById(R.id.ll_select);
        mImgClose = view.findViewById(R.id.img_close);
        this.setContentView(view);
        this.setBackgroundDrawable(new BitmapDrawable());

        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        int height = (int) (wm.getDefaultDisplay().getHeight() * 0.8);
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        this.setHeight(height);
        this.setFocusable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            this.setClippingEnabled(false);
            this.setOutsideTouchable(false);
        }
        this.setAnimationStyle(R.style.PopupWindow);

        String data =  com.miles.zcstc.fingerdemo.JsonParser.getJson(context, "city.json");
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonElements = jsonParser.parse(data).getAsJsonArray();//获取JsonArray对象
        beans = new ArrayList<>();
        Gson gson = new Gson();
        for (JsonElement bean : jsonElements) {
            CityInfoBean bean1 = gson.fromJson(bean, CityInfoBean.class);//解析
            beans.add(bean1);
        }
        initData(beans);

        mImgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mTvProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                province = "";
                city = "";
                mTvCity.setVisibility(View.GONE);
                mTvProvince.setVisibility(View.GONE);
                mLlSelect.setVisibility(View.VISIBLE);

                mRvProvince.setVisibility(View.VISIBLE);
                mRvCity.setVisibility(View.GONE);
                mRvArea.setVisibility(View.GONE);
                cityList.clear();
            }
        });

        mTvCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                city = "";
                mTvCity.setVisibility(View.GONE);
                areaList.clear();
//                initCity();
                mRvProvince.setVisibility(View.GONE);
                mRvCity.setVisibility(View.VISIBLE);
                mRvArea.setVisibility(View.GONE);
            }
        });

       /* mTvArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initArea();
            }
        });*/
    }

    private void initData(ArrayList<CityInfoBean> beans) {
        for (int i = 0; i < beans.size(); i++) {
            provinceList.add(beans.get(i).getName());
        }
        initProvince();
    }

    private void initProvince() {
        mRvProvince.setVisibility(View.VISIBLE);
        mRvCity.setVisibility(View.GONE);
        mRvArea.setVisibility(View.GONE);
        mProvinceAdapter = new ProvinceAdapter(provinceList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRvProvince.setLayoutManager(linearLayoutManager);
        mRvProvince.setAdapter(mProvinceAdapter);
        mProvinceAdapter.setOnItemListener(new ProvinceAdapter.onItemClick() {
            @Override
            public void onClick(int position, String name) {
                mProvinceAdapter.setSelection(position);
                mProvinceAdapter.notifyDataSetChanged();
                if (TextUtils.isEmpty(province)) {
                    province = name;
                    mTvProvince.setVisibility(View.VISIBLE);
                    mTvProvince.setText(name);
                    initCity();
                }
                mCityInfoBean = beans.get(position);
                for (int i = 0; i < mCityInfoBean.getCity().size(); i++) {
                    cityList.add(mCityInfoBean.getCity().get(i).getName());
                }
            }
        });
        mProvinceAdapter.notifyDataSetChanged();
    }

    private void initCity() {
        mRvProvince.setVisibility(View.GONE);
        mRvCity.setVisibility(View.VISIBLE);
        mRvArea.setVisibility(View.GONE);
        mCityAdapter = new CityAdapter(cityList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRvCity.setLayoutManager(linearLayoutManager);
        mRvCity.setAdapter(mCityAdapter);
        mCityAdapter.setOnItemListener(new CityAdapter.onItemClick() {
            @Override
            public void onClick(int position, String name) {
                if (TextUtils.isEmpty(city)) {
                    city = name;
                    mTvCity.setVisibility(View.VISIBLE);
                    mTvCity.setText(city);
                }
                areaList = mCityInfoBean.getCity().get(position).getArea();
                initArea();
            }
        });
    }

    private void initArea() {
        mRvProvince.setVisibility(View.GONE);
        mRvCity.setVisibility(View.GONE);
        mRvArea.setVisibility(View.VISIBLE);
        mAreaAdapter = new AreaAdapter(areaList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRvArea.setLayoutManager(linearLayoutManager);
        mRvArea.setAdapter(mAreaAdapter);
        mAreaAdapter.setOnItemListener(new AreaAdapter.onItemClick() {
            @Override
            public void onClick(int position, String name) {
                if (TextUtils.isEmpty(area)) {
                    area = name;
                    mTvArea.setVisibility(View.VISIBLE);
                    mTvArea.setText(area);
                    mLlSelect.setVisibility(View.GONE);
                    citySelect.onSelect(province, city, area);
                    dismiss();
                }
            }
        });
    }

    public interface onCitySelect {
        void onSelect(String province, String city, String area);
    }
}
