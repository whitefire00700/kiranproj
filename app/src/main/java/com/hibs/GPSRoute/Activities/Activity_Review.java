package com.hibs.GPSRoute.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.hibs.GPSRoute.Api_Task.GetReview_Process;
import com.hibs.GPSRoute.Database.SqliteReview;
import com.hibs.GPSRoute.Listeners.GetReview_Listener;
import com.hibs.GPSRoute.Pojo.Review;
import com.hibs.GPSRoute.R;
import com.hibs.GPSRoute.Utils.Utility;

import java.util.ArrayList;

public class Activity_Review extends AppCompatActivity implements View.OnClickListener, GetReview_Listener {

    private Activity mActivity;
    private RatingBar ratingBar_Yours;
    private LinearLayout llYourReview, ll_review_list;
    private EditText etYourReview;
    private TextView tvYourReview;
    private ImageView ivBack;
    private Button btnSave;
    private String placeId;
    private SqliteReview sqliteReview;
    private Utility utility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        initUI();
    }

    private void initUI() {
        mActivity = Activity_Review.this;
        utility = new Utility(mActivity);
        sqliteReview = new SqliteReview(mActivity);
        placeId = getIntent().getStringExtra("placeId");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ratingBar_Yours = (RatingBar) findViewById(R.id.rb_urs);
        llYourReview = (LinearLayout) findViewById(R.id.ll_ur_review);
        ll_review_list = (LinearLayout) findViewById(R.id.ll_review_list);
        etYourReview = (EditText) findViewById(R.id.et_ur_review);
        tvYourReview = (TextView) findViewById(R.id.tv_show_review);
        btnSave = (Button) findViewById(R.id.btnsave);
        ivBack=(ImageView) findViewById(R.id.iv_back);
        initListeners();
    }

    private void initListeners() {
        tvYourReview.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        getReviews();
    }

    private void getReviews() {
        utility.showProgress("", "loading..");
        GetReview_Process getReview_process = new GetReview_Process(mActivity, Activity_Review.this, placeId);
        getReview_process.GetReviews();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnsave:
                saveOrUpdateReview();
                break;
            case R.id.tv_show_review:
                showReview();
                break;
            case R.id.iv_back:
               finish();
                break;
            default:
                break;
        }

    }

    private void showReview() {
        if (llYourReview.getVisibility() == View.GONE) {
            llYourReview.setVisibility(View.VISIBLE);
            if (sqliteReview.checkReviewExists(placeId)) {
                btnSave.setText("Update");
                ArrayList<String> arrayList = sqliteReview.getReview(placeId);
                String review = arrayList.get(1);
                float rating = Float.parseFloat(arrayList.get(0));
                etYourReview.setText(review);
                ratingBar_Yours.setRating(rating);
            }
        } else {
            llYourReview.setVisibility(View.GONE);
        }
    }

    private void saveOrUpdateReview() {
        String review = etYourReview.getText().toString().trim();
        String rating = "" + ratingBar_Yours.getRating();
        if (sqliteReview.checkReviewExists(placeId)) {
            btnSave.setText("Update");
            int flag = sqliteReview.updateReview(placeId, rating, review);
            if (flag != -1) {
                Utility.alertBox(mActivity, "Updated successfully!");
            } else {
                Utility.alertBox(mActivity, "Review isn't updated. Please try again later!");
            }
        } else {
            btnSave.setText("Save");
            int flag = sqliteReview.addReview(placeId, rating, review);
            if (flag != -1) {
                Utility.alertBox(mActivity, "Added successfully!");
            } else {
                Utility.alertBox(mActivity, "Review isn't added. Please try again later!");
            }
        }
    }

    private void addReviewList(ArrayList<Review> list) {
        ll_review_list.removeAllViews();

        for (int i = 0; i < list.size(); i++) {
            LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.review_item, null);
            RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
            TextView tvName = (TextView) view.findViewById(R.id.tv_name);
            TextView tvReview = (TextView) view.findViewById(R.id.tv_review);

            Review reviewObj = list.get(i);
            float rating = Float.parseFloat(reviewObj.getRating());
            String name = reviewObj.getName();
            String review = reviewObj.getReview();

            if (review.length() == 0) {
                tvReview.setVisibility(View.GONE);
            }

            ratingBar.setRating(rating);
            tvName.setText(name);
            tvReview.setText(review);
            ll_review_list.addView(view);
        }

    }

    @Override
    public void onReviewsReceived(ArrayList<Review> list) {
        utility.hideProgress();
        addReviewList(list);
    }

    @Override
    public void onReviewsReceivedError(String Str_Message) {
        utility.hideProgress();
        Utility.alertBox(mActivity, "No reviews available!");
    }
}
