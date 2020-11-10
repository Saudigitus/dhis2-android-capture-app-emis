package org.dhis2.data.forms.dataentry.fields.spinner;

import android.view.View;

import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.MutableLiveData;

import org.dhis2.data.forms.dataentry.fields.FieldViewModel;
import org.dhis2.data.forms.dataentry.fields.FormViewHolder;
import org.dhis2.data.forms.dataentry.fields.RowAction;
import org.dhis2.databinding.FormOptionSetBinding;
import org.dhis2.utils.optionset.OptionSetDialog;
import org.dhis2.utils.customviews.OptionSetPopUp;

import io.reactivex.processors.FlowableProcessor;

/**
 * QUADRAM. Created by ppajuelo on 07/11/2017.
 */

public class SpinnerHolder extends FormViewHolder implements View.OnClickListener {

    private final boolean isSearchMode;
    private FormOptionSetBinding binding;

    private SpinnerViewModel viewModel;

    public SpinnerHolder(ViewDataBinding binding, FlowableProcessor<RowAction> processor, boolean isSearchMode, MutableLiveData<String> currentSelection) {
        super(binding);
        this.binding = (FormOptionSetBinding) binding;
        this.isSearchMode = isSearchMode;
        this.currentUid = currentSelection;

        this.binding.optionSetView.setOnSelectedOptionListener((optionName, optionCode) -> {
            processor.onNext(
                    RowAction.create(viewModel.uid(), isSearchMode ? optionName + "_os_" + optionCode : optionCode, true, optionCode, optionName, getAdapterPosition())
            );
            if (isSearchMode)
                viewModel.withValue(optionName);
            clearBackground(isSearchMode);
        });

        this.binding.optionSetView.setActivationListener(() -> setSelectedBackground(isSearchMode));

    }

    @Override
    public void update(FieldViewModel fieldViewModel) {
        this.viewModel = (SpinnerViewModel) fieldViewModel;
        fieldUid = viewModel.uid();
        binding.optionSetView.setNumberOfOptions(viewModel.numberOfOptions());
        binding.optionSetView.updateEditable(viewModel.editable());
        binding.optionSetView.setValue(viewModel.value());
        binding.optionSetView.setWarning(viewModel.warning(), viewModel.error());
        binding.optionSetView.setLabel(viewModel.label(), viewModel.mandatory());
        descriptionText = viewModel.description();
        binding.optionSetView.setDescription(descriptionText);
        binding.optionSetView.setOnClickListener(this);
        label = new StringBuilder().append(viewModel.label());
        initFieldFocus();
        setFormFieldBackground();
    }

    @Override
    public void onClick(View v) {
        binding.optionSetView.requestFocus();
        closeKeyboard(v);
        setSelectedBackground(isSearchMode);
        OptionSetDialog dialog = new OptionSetDialog();
        dialog.create(itemView.getContext());
        dialog.setOptionSet(viewModel);

        if (dialog.showDialog()) {
            dialog.setListener(binding.optionSetView);
            dialog.setClearListener((view) -> binding.optionSetView.deleteSelectedOption());
            dialog.show(((FragmentActivity) binding.getRoot().getContext()).getSupportFragmentManager(), OptionSetDialog.Companion.getTAG());
        } else {
            dialog.dismiss();
            new OptionSetPopUp(itemView.getContext(), v, viewModel,
                    binding.optionSetView);
        }
    }
}
