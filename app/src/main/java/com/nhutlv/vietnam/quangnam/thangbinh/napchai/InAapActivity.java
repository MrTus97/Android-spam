package com.nhutlv.vietnam.quangnam.thangbinh.napchai;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchaseHistoryResponseListener;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InAapActivity extends AppCompatActivity {

    private Map<String, SkuDetails> mSkuDetailsMap = new HashMap<>();
    private String mSkuIdSub1 = SkuConstant.ITEM_TO_BUY_SKU_ID_1;
    private String mSkuIdSub2 = SkuConstant.ITEM_TO_BUY_SKU_ID_2;
    private BillingClient mBillingClient;
    private TextView price_Sub2;
    private TextView price_Sub1;
    private TextView tvTitle;
    private ImageButton btnBack;

    private CardView cardViewSub1;
    private CardView cardViewSub2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_in_app);

        SessionManager.getInstance().init(this);

        price_Sub2 = findViewById(R.id.tvPriceYear);
        price_Sub1 = findViewById(R.id.tvPriceWeek);
        tvTitle = findViewById(R.id.tvTitle);
        btnBack = findViewById(R.id.btnBack);

        cardViewSub1 = findViewById(R.id.week);
        cardViewSub2 = findViewById(R.id.year);

        tvTitle.setText("InApp");
        initBilling();

        btnBack.setOnClickListener(v -> onBackPressed());

        cardViewSub2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchBilling(mSkuIdSub2);

            }
        });

        cardViewSub1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchBilling(mSkuIdSub1);

            }
        });

    }

    private void initBilling() {
        mBillingClient = BillingClient.newBuilder(this).setListener(new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(int billingResult, @Nullable List<Purchase> purchases) {
                if (billingResult == BillingClient.BillingResponse.OK) {
                    processPurchases(purchases);
                }
            }
        }).build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(int billingResult) {
                if (billingResult == BillingClient.BillingResponse.OK) {
                    querySkuDetails(); //query for products
                    List<Purchase> purchasesList = queryPurchases(); //query for purchases
                    processPurchases(purchasesList);
                    updatePriceInUi();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                //here when something went wrong, e.g. no internet connection
            }
        });
    }

    private void querySkuDetails() {
        SkuDetailsParams.Builder skuDetailsParamsBuilder = SkuDetailsParams.newBuilder();
        List<String> skuList = new ArrayList<>();
        skuList.add(mSkuIdSub1);
        skuList.add(mSkuIdSub2);
        skuDetailsParamsBuilder.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
        if (mBillingClient != null) {
            try {
                mBillingClient.querySkuDetailsAsync(skuDetailsParamsBuilder.build(), new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(int billingResult, List<SkuDetails> skuDetailsList) {
                        if (billingResult == 0) {
                            if (skuDetailsList != null && !skuDetailsList.isEmpty()) {
                                for (SkuDetails skuDetails : skuDetailsList) {
                                    mSkuDetailsMap.put(skuDetails.getSku(), skuDetails);
                                }
                            }
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updatePriceInUi() {
        List<String> skuList = new ArrayList<>();
        skuList.add(SkuConstant.ITEM_TO_BUY_SKU_ID_1);
        skuList.add(SkuConstant.ITEM_TO_BUY_SKU_ID_2);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(BillingClient.SkuType.SUBS);
        try {
            if (mBillingClient != null) {
                mBillingClient.querySkuDetailsAsync(params.build(),
                        new SkuDetailsResponseListener() {
                            @Override
                            public void onSkuDetailsResponse(int billingResult, List<SkuDetails> skuDetailsList) {
                                if (!skuDetailsList.isEmpty()) {
                                    for (SkuDetails detail : skuDetailsList) {
                                        if (detail.getSku().equals(SkuConstant.ITEM_TO_BUY_SKU_ID_1)) {
                                            price_Sub2.setText(detail.getPrice());
                                        } else if (detail.getSku().equals(SkuConstant.ITEM_TO_BUY_SKU_ID_2)) {
                                            price_Sub1.setText(detail.getPrice());
                                        }
                                    }
                                }
                            }
                        });

            } else {
                price_Sub1.setText("4.99$");
                price_Sub2.setText("4.99$");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Purchase> queryPurchases() {
        Purchase.PurchasesResult purchasesResult = mBillingClient.queryPurchases(BillingClient.SkuType.SUBS);
        return purchasesResult.getPurchasesList();
    }

    public void launchBilling(String skuId) {
        mBillingClient.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS, new PurchaseHistoryResponseListener() {
            @Override
            public void onPurchaseHistoryResponse(int responseCode, List<Purchase> purchasesList) {
                if (responseCode == BillingClient.BillingResponse.OK) {
                    if (purchasesList.size() > 0) {
                        SessionManager.getInstance().setKeySaveBuyInApp("buy");
                    }
                }
            }
        });

        BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                .setSku(skuId)
                .setType(BillingClient.SkuType.SUBS)
                .build();
        mBillingClient.launchBillingFlow(this, billingFlowParams);
    }

    private void processPurchases(List<Purchase> purchases) {
        for (Purchase purchase : purchases) {
            if (TextUtils.equals(mSkuIdSub1, purchase.getSku()) || TextUtils.equals(mSkuIdSub2, purchase.getSku())) {
                payComplete();
            }
        }
    }

    private void payComplete() {
        SessionManager.getInstance().setKeySaveBuyInApp("buy");
        Intent intent = new Intent(InAapActivity.this, MainActivity.class);
        intent.putExtra("requestSystemAlert", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(InAapActivity.this, MainActivity.class).putExtra("requestSystemAlert", true).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }
}