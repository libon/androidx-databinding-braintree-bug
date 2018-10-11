package com.example.myapplication;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.Card;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.CardBuilder;
import com.braintreepayments.api.models.PaymentMethodNonce;
import com.example.myapplication.databinding.ActivityMainBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;
import androidx.databinding.ObservableField;

public class MainActivity extends AppCompatActivity implements PaymentMethodNonceCreatedListener,
        BraintreeErrorListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    // Stolen from: https://github.com/braintree/braintree_android/blob/master/Demo/src/main/java/com/braintreepayments/demo/Settings.java#L19
    private static final String SANDBOX_TOKENIZATION_KEY = "sandbox_tmxhyf7d_dcpspy2brwdjr3qn";
    private BraintreeFragment mBraintreeFragment;

    // Should be a viewmodel but just going for simplicity to reproduce the bug
    public static class Model {
        public ObservableField<CharSequence> inputText = new ObservableField<>();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        final Model model = new Model();

        binding.setModel(model);
        model.inputText.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Log.v(TAG, "inputText changed: " + model.inputText.get());
            }
        });

        try {
            mBraintreeFragment = BraintreeFragment.newInstance(this,
                    SANDBOX_TOKENIZATION_KEY);
        } catch (InvalidArgumentException e) {
            onError(e);
        }

        Card.tokenize(mBraintreeFragment, new CardBuilder()
                        .cardNumber("4111111111111111"));
    }

    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
        Toast.makeText(this, "Success " + paymentMethodNonce.getNonce(),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Exception error) {
        throw new RuntimeException(error);
    }
}
