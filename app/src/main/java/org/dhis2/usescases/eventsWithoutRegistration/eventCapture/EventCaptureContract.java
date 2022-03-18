package org.dhis2.usescases.eventsWithoutRegistration.eventCapture;

import org.dhis2.commons.data.FieldWithIssue;
import org.dhis2.usescases.eventsWithoutRegistration.eventCapture.model.EventCompletionDialog;
import org.dhis2.usescases.general.AbstractActivityContracts;
import org.hisp.dhis.android.core.event.EventStatus;
import org.hisp.dhis.android.core.organisationunit.OrganisationUnit;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;
import java.util.Map;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Single;

public class EventCaptureContract {

    public interface View extends AbstractActivityContracts.View {

        void renderInitialInfo(String stageName, String eventDate, String orgUnit, String catOption);

        EventCaptureContract.Presenter getPresenter();

        void updatePercentage(float primaryValue);

        void showCompleteActions(
                boolean canComplete,
                Map<String, String> emptyMandatoryFields,
                EventCompletionDialog eventCompletionDialog);

        void restartDataEntry();

        void finishDataEntry();

        void attemptToReopen();

        void showSnackBar(int messageId);

        void clearFocus();

        void attemptToSkip();

        void attemptToReschedule();

        void setProgramStage(String programStageUid);

        void showErrorSnackBar();

        void showEventIntegrityAlert();

        void updateNoteBadge(int numberOfNotes);

        void goBack();

        void showProgress();

        void hideProgress();

        void showNavigationBar();

        void hideNavigationBar();
    }

    public interface Presenter extends AbstractActivityContracts.Presenter {

        void init();

        void onBackClick();

        void attemptFinish(boolean canComplete,
                           String onCompleteMessage,
                           List<FieldWithIssue> errorFields,
                           Map<String, String> emptyMandatoryFields,
                           List<FieldWithIssue> warningFields);

        boolean isEnrollmentOpen();

        void completeEvent(boolean addNew);

        void reopenEvent();

        void deleteEvent();

        void skipEvent();

        void rescheduleEvent(Date time);

        boolean canWrite();

        boolean hasExpired();

        void saveImage(String uuid, String filePath);

        void initNoteCounter();

        void refreshTabCounters();

        void hideProgress();

        void showProgress();

        boolean getCompletionPercentageVisibility();

        void setValueChanged(@NotNull String uid);
    }

    public interface EventCaptureRepository {

        Flowable<Boolean> eventIntegrityCheck();

        Flowable<String> programStageName();

        Flowable<String> eventDate();

        Flowable<OrganisationUnit> orgUnit();

        Flowable<String> catOption();

        Observable<Boolean> completeEvent();

        Flowable<EventStatus> eventStatus();

        boolean reopenEvent();

        boolean isEnrollmentOpen();

        Observable<Boolean> deleteEvent();

        Observable<Boolean> updateEventStatus(EventStatus skipped);

        Observable<Boolean> rescheduleEvent(Date time);

        Observable<String> programStage();

        boolean getAccessDataWrite();

        boolean isEnrollmentCancelled();

        boolean isEventEditable(String eventUid);

        Single<Boolean> canReOpenEvent();

        Observable<Boolean> isCompletedEventExpired(String eventUid);

        Single<Integer> getNoteCount();

        boolean showCompletionPercentage();

        boolean hasAnalytics();

        boolean hasRelationships();
    }

}
