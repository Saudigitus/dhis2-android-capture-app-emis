package com.dhis2.usescases.teiDashboard.dashboardfragments;

import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.dhis2.R;
import com.dhis2.databinding.FragmentScheduleBinding;
import com.dhis2.usescases.general.FragmentGlobalAbstract;
import com.dhis2.usescases.teiDashboard.DashboardProgramModel;
import com.dhis2.usescases.teiDashboard.adapters.ScheduleAdapter;

import org.hisp.dhis.android.core.enrollment.Enrollment;
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance;

/**
 * Created by ppajuelo on 29/11/2017.
 */

public class ScheduleFragment extends FragmentGlobalAbstract implements View.OnClickListener {

    FragmentScheduleBinding binding;

    static ScheduleFragment instance;
    private static TrackedEntityInstance trackedEntity;
    private static DashboardProgramModel program;

    public static ScheduleFragment getInstance() {
        if (instance == null)
            instance = new ScheduleFragment();
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_schedule, container, false);
        for (Enrollment enrollment : trackedEntity.enrollments())
            if (enrollment.program().equals(program.getProgram().uid()))
                binding.scheduleRecycler.setAdapter(new ScheduleAdapter(program.getProgramStages(), enrollment.events()));
        onResume();
        binding.scheduleFilter.setOnClickListener(this);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        instance = null;
        super.onDestroy();
    }

    public void setData(TrackedEntityInstance trackedEntityModel, DashboardProgramModel mprogram) {
        trackedEntity = trackedEntityModel;
        program = mprogram;
    }

    @Override
    public void onClick(View view) {
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.ic_filter_list);
        int color;
        switch (((ScheduleAdapter) binding.scheduleRecycler.getAdapter()).filter()) {
            case SCHEDULE:
                color = ContextCompat.getColor(view.getContext(), R.color.green_7ed);
                break;
            case OVERDUE:
                color = ContextCompat.getColor(view.getContext(), R.color.red_060);
                break;
            default:
                TypedValue typedValue = new TypedValue();
                TypedArray a = view.getContext().obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
                color = a.getColor(0, 0);
                a.recycle();
                break;
        }
        drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN ));
        binding.scheduleFilter.setImageDrawable(drawable);
    }
}
