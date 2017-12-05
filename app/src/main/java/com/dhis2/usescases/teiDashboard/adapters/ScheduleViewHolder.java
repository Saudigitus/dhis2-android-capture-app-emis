package com.dhis2.usescases.teiDashboard.adapters;

import android.support.v7.widget.RecyclerView;

import com.dhis2.BR;
import com.dhis2.databinding.ItemScheduleBinding;

import org.hisp.dhis.android.core.event.Event;
import org.hisp.dhis.android.core.program.ProgramStageModel;

/**
 * Created by ppajuelo on 29/11/2017.
 */

class ScheduleViewHolder extends RecyclerView.ViewHolder {
    ItemScheduleBinding binding;

    public ScheduleViewHolder(ItemScheduleBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Event eventModel, boolean isFirst, boolean isLast, ProgramStageModel programStage) {
        binding.setVariable(BR.event, eventModel);
        binding.setVariable(BR.isfirst, isFirst);
        binding.setVariable(BR.islast, isLast);
        binding.setVariable(BR.stage, programStage);
        binding.executePendingBindings();
    }
}
