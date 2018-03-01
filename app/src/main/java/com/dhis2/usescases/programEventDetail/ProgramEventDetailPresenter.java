package com.dhis2.usescases.programEventDetail;

import android.os.Bundle;

import com.dhis2.usescases.searchTrackEntity.SearchTEActivity;
import com.dhis2.usescases.teiDashboard.mobile.TeiDashboardMobileActivity;
import com.dhis2.utils.Period;

import org.hisp.dhis.android.core.category.CategoryOptionComboModel;
import org.hisp.dhis.android.core.program.ProgramModel;

import java.util.Date;
import java.util.List;

/**
 * Created by Cristian on 13/02/2018.
 *
 */

public class ProgramEventDetailPresenter implements ProgramEventDetailContract.Presenter {

    static private ProgramEventDetailContract.View view;
    private final ProgramEventDetailContract.Interactor interactor;
    public ProgramModel program;

    ProgramEventDetailPresenter(ProgramEventDetailContract.Interactor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void init(ProgramEventDetailContract.View mview, String programId) {
        view = mview;
        interactor.init(view, programId);
    }

    @Override
    public void onTimeButtonClick() {
        view.showTimeUnitPicker();
    }

    @Override
    public void onDateRangeButtonClick() {
        view.showRageDatePicker();
    }

    @Override
    public void onOrgUnitButtonClick() {
        view.openDrawer();
    }

    @Override
    public void setProgram(ProgramModel program) {
        this.program = program;
    }

    @Override
    public void getEvents(Date fromDate, Date toDate) {
        interactor.getEvents(program.uid(), fromDate, toDate);
    }

    @Override
    public void getProgramEventsWithDates(List<Date> dates, Period period) {
        interactor.getProgramEventsWithDates(program.uid(), dates, period);
    }

    @Override
    public void onCatComboSelected(CategoryOptionComboModel categoryOptionComboModel) {
        interactor.updateFilters(categoryOptionComboModel);
    }

    @Override
    public void clearCatComboFilters() {
        interactor.updateFilters(null);
    }

    @Override
    public void onSearchClick() {
        Bundle bundle = new Bundle();
        //bundle.putString("TRACKED_ENTITY_UID", program.trackedEntityType());
        view.startActivity(SearchTEActivity.class, bundle, false, false, null);
    }

    public void addEvent() {
        //TODO: Implement Event Creation 'Screen 0'
    }

    @Override
    public void onBackClick() {
        view.back();
    }

    @Override
    public void onDettach() {
        interactor.onDettach();
    }

    @Override
    public void onTEIClick(String TEIuid, String programUid) {
        Bundle bundle = new Bundle();
        bundle.putString("TEI_UID", TEIuid);
        bundle.putString("PROGRAM_UID", programUid);
        view.startActivity(TeiDashboardMobileActivity.class, bundle, false, false, null);
    }
}
