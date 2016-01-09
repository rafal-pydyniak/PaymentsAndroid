package pl.pydyniak.payments;

import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * Created by rafal on 31.12.15.
 */
public class PaymentHolder {
    RelativeLayout basicRowRelativeLayout;
    TextView nameText;
    TextView dateText;
    RelativeLayout extendedRowRelativeLayout;
    TextView descriptionLabel;
    TextView descriptionText;

    public PaymentHolder(RelativeLayout basicRowRelativeLayout, TextView nameText, TextView dateText,
                         RelativeLayout extendedRowRelativeLayout, TextView descriptionLabel, TextView descriptionText) {
        this.basicRowRelativeLayout = basicRowRelativeLayout;
        this.nameText = nameText;
        this.dateText = dateText;
        this.extendedRowRelativeLayout = extendedRowRelativeLayout;
        this.descriptionLabel = descriptionLabel;
        this.descriptionText = descriptionText;
    }

    public RelativeLayout getBasicRowRelativeLayout() {
        return basicRowRelativeLayout;
    }

    public void setBasicRowRelativeLayout(RelativeLayout basicRowRelativeLayout) {
        this.basicRowRelativeLayout = basicRowRelativeLayout;
    }

    public TextView getNameText() {
        return nameText;
    }

    public void setNameText(TextView nameText) {
        this.nameText = nameText;
    }

    public TextView getDateText() {
        return dateText;
    }

    public void setDateText(TextView dateText) {
        this.dateText = dateText;
    }

    public RelativeLayout getExtendedRowRelativeLayout() {
        return extendedRowRelativeLayout;
    }

    public void setExtendedRowRelativeLayout(RelativeLayout extendedRowRelativeLayout) {
        this.extendedRowRelativeLayout = extendedRowRelativeLayout;
    }

    public TextView getDescriptionLabel() {
        return descriptionLabel;
    }

    public void setDescriptionLabel(TextView descriptionLabel) {
        this.descriptionLabel = descriptionLabel;
    }

    public TextView getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(TextView descriptionText) {
        this.descriptionText = descriptionText;
    }
}
