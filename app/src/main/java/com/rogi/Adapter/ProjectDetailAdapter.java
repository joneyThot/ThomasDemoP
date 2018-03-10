package com.rogi.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rogi.Activity.ProjectDetailsActivity;
import com.rogi.Model.TaskModel;
import com.rogi.R;
import com.rogi.View.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by "Mayuri" on 21/7/17.
 */
public class ProjectDetailAdapter extends RecyclerView.Adapter<ProjectDetailAdapter.ViewHolder> implements OnMapReadyCallback {
    List<TaskModel> tasks;
    List<TaskModel> notesArray;
    private LayoutInflater inflater;
    private Context context;
    MediaAdapter mediaAdapter;
    AdditionalContactListAdapter additionalContactListAdapter;
    NoteListAdapter noteListAdapter;
    private GoogleMap googleMap;
    private LatLng location;
    double latitude = 0, longitude = 0;
    boolean additionalFlag = false, noteFlag = false, projectFlag = false;
    private SharedPreferences mSharedPreferences;

    final static private int TASK_WEBSITE_VIEW = 14;
    final static private int TASK_TITLE_VIEW = 0;
    final static private int TASK_CONTACT_VIEW = 1;
    final static private int TASK_TIMING_VIEW = 2;
    final static private int TASK_ADDRESS_VIEW = 3;
    final static private int TASK_ADDITIONAL_CONTACT_TITLE_VIEW = 4;
    final static private int TASK_ADDITIONAL_CONTACT_VIEW = 5;
    final static private int TASK_NOTE_TITLE_VIEW = 6;
    final static private int TASK_NOTE_VIEW = 7;
    final static private int TASK_PROJECT_PHASE_TITLE_VIEW = 8;
    final static private int TASK_PROJECT_PHASE_VIEW = 9;
    final static private int TASK_MEDIA_TITLE_VIEW = 10;
    final static private int TASK_MEDIA_VIEW = 11;
    final static private int TASK_STATUS_BTN_VIEW = 12;
    final static private int TASK_GENERATE_REPORT_BTN_VIEW = 13;

    View.OnClickListener updateDueDateTimeClick;
    View.OnClickListener addNoteClick;
    View.OnClickListener addMediaClick;
    NoteListAdapter.OnEditNoteListner editNoteClick;
    NoteListAdapter.OnDeleteNoteListner deleteNoteClick;
    ProjectPhaseListAdapter.OnEditProjectPhaseListner onEditProjectPhaseListner;
    ProjectPhaseListAdapter.OnDeleteProjectPhaseListner onDeleteProjectPhaseListner;
    View.OnClickListener addProjectClick;
    View.OnClickListener addAdditionalContactClick;
    AdditionalContactListAdapter.OnEditAdditionalContactListner editAdditionalContactClick;
    AdditionalContactListAdapter.OnDeleteAdditionalContactListner deleteAdditionalContactClick;
    MediaAdapter.OnMediaClickListner mediaClick;
    MediaAdapter.OnMediaLongClickListner mediaLongClick;
    View.OnClickListener updateStatusClick;
    View.OnClickListener generateReportClick;
    View.OnClickListener phoneClick;
    String isTablet = "";

    public ProjectDetailAdapter(List<TaskModel> tasks, Context context, View.OnClickListener updateDueDateTimeClick, View.OnClickListener addNoteClick,
                                NoteListAdapter.OnEditNoteListner editNoteClick, NoteListAdapter.OnDeleteNoteListner deleteNoteClick,
                                View.OnClickListener addProjectClick, View.OnClickListener addAdditionalContactClick,
                                AdditionalContactListAdapter.OnEditAdditionalContactListner editAdditionalContactClick,
                                AdditionalContactListAdapter.OnDeleteAdditionalContactListner deleteAdditionalContactClick,
                                ProjectPhaseListAdapter.OnEditProjectPhaseListner onEditProjectPhaseListner,
                                ProjectPhaseListAdapter.OnDeleteProjectPhaseListner onDeleteProjectPhaseListner,
                                View.OnClickListener addMediaClick, MediaAdapter.OnMediaClickListner mediaClick,
                                MediaAdapter.OnMediaLongClickListner mediaLongClick, View.OnClickListener updateStatusClick,
                                View.OnClickListener generateReportClick, View.OnClickListener phoneClick, String isTablet) {
        super();
        inflater = LayoutInflater.from(context);
        this.context = context;
        this.tasks = tasks;
        this.updateDueDateTimeClick = updateDueDateTimeClick;
        this.addNoteClick = addNoteClick;
        this.editNoteClick = editNoteClick;
        this.deleteNoteClick = deleteNoteClick;
        this.addProjectClick = addProjectClick;
        this.addAdditionalContactClick = addAdditionalContactClick;
        this.editAdditionalContactClick = editAdditionalContactClick;
        this.deleteAdditionalContactClick = deleteAdditionalContactClick;
        this.onEditProjectPhaseListner = onEditProjectPhaseListner;
        this.onDeleteProjectPhaseListner = onDeleteProjectPhaseListner;
        this.addMediaClick = addMediaClick;
        this.mediaClick = mediaClick;
        this.mediaLongClick = mediaLongClick;
        this.updateStatusClick = updateStatusClick;
        this.generateReportClick = generateReportClick;
        this.phoneClick = phoneClick;
        this.isTablet = isTablet;
        mSharedPreferences = context.getSharedPreferences(Utils.PREF_NAME, context.MODE_PRIVATE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v12 = inflater.inflate(R.layout.activity_task_website_view, parent, false);
        View v1 = inflater.inflate(R.layout.activity_task_title_view, parent, false);
        View v2 = inflater.inflate(R.layout.activity_task_contact_view, parent, false);
        View v3 = inflater.inflate(R.layout.activity_task_timing_view, parent, false);
        View v4 = inflater.inflate(R.layout.activity_task_address_view, parent, false);
        View v6 = inflater.inflate(R.layout.activity_task_additional_contact_view, parent, false);
        View v7 = inflater.inflate(R.layout.activity_task_note_view, parent, false);
        View v8 = inflater.inflate(R.layout.activity_task_project_phase_view, parent, false);
        View v9 = inflater.inflate(R.layout.activity_task_media_title_view, parent, false);
        View v10 = inflater.inflate(R.layout.activity_task_status_btn_view, parent, false);
        View v11 = inflater.inflate(R.layout.activity_task_status_btn_view, parent, false);

        RecyclerView.ViewHolder taskWebsite = new ViewHolder(v12);
        RecyclerView.ViewHolder taskTitle = new ViewHolder(v1);
        RecyclerView.ViewHolder taskContact = new ViewHolder(v2);
        RecyclerView.ViewHolder taskTiming = new ViewHolder(v3);
        RecyclerView.ViewHolder taskAddress = new ViewHolder(v4);
        RecyclerView.ViewHolder taskAdditionalContact = new ViewHolder(v6);
        RecyclerView.ViewHolder taskNoteView = new ViewHolder(v7);
        RecyclerView.ViewHolder taskProjectPhase = new ViewHolder(v8);
        RecyclerView.ViewHolder taskMediaTitle = new ViewHolder(v9);
        RecyclerView.ViewHolder taskStatusbtn = new ViewHolder(v10);
        RecyclerView.ViewHolder taskReportbtn = new ViewHolder(v11);

        switch (viewType) {
            case TASK_WEBSITE_VIEW:
                return (ViewHolder) taskWebsite;
            case TASK_TITLE_VIEW:
                return (ViewHolder) taskTitle;
            case TASK_CONTACT_VIEW:
                return (ViewHolder) taskContact;
            case TASK_TIMING_VIEW:
                return (ViewHolder) taskTiming;
            case TASK_ADDRESS_VIEW:
                return (ViewHolder) taskAddress;
            case TASK_ADDITIONAL_CONTACT_VIEW:
                return (ViewHolder) taskAdditionalContact;
            case TASK_NOTE_VIEW:
                return (ViewHolder) taskNoteView;
            case TASK_PROJECT_PHASE_VIEW:
                return (ViewHolder) taskProjectPhase;
            case TASK_MEDIA_VIEW:
                return (ViewHolder) taskMediaTitle;
            case TASK_STATUS_BTN_VIEW:
                return (ViewHolder) taskStatusbtn;
            case TASK_GENERATE_REPORT_BTN_VIEW:
                return (ViewHolder) taskReportbtn;

            default:
                return (ViewHolder) taskTitle;
        }
    }


    @Override
    public int getItemCount() {
        return tasks.size();
    }

    @Override
    public int getItemViewType(int position) {
        return tasks.get(position).getViewType();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final TaskModel taskItems = tasks.get(position);
        inflater = LayoutInflater.from(context);
        notesArray = new ArrayList<>();

        int TASK_VIEW_TYPE = taskItems.getViewType();

        if (TASK_VIEW_TYPE == TASK_WEBSITE_VIEW) {
            if (Utils.validateString(Utils.WEB_SITE_URL)) {
                //Login to my rogi.com for add’l project changes and edits
                String html = "Login to <a href=\"" + Utils.WEB_SITE_URL + "\">myrogi.com</a> for add’l project changes and edits";
                Spanned result;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
                } else {
                    result = Html.fromHtml(html);
                }
                holder.txtRogiUrl.setText(result);
                holder.txtRogiUrl.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }

        if (TASK_VIEW_TYPE == TASK_TITLE_VIEW) {
            holder.taskTitleText.setText(taskItems.getTitle());
            holder.taskDescriptionText.setText(taskItems.getDescription());
            if (Utils.validateString(taskItems.getPriority())) {
                String priority = taskItems.getPriority();
                if (priority.equalsIgnoreCase("High")) {
                    holder.priorityBTN.setBackgroundResource(R.color.highPriorityColor);
                    holder.priorityBTN.setText(taskItems.getPriority());
                } else {
                    holder.priorityBTN.setBackgroundResource(R.color.colorGreen);
                    holder.priorityBTN.setText(taskItems.getPriority());
                }
            }

        }

        if (TASK_VIEW_TYPE == TASK_CONTACT_VIEW) {
            holder.assignNameText.setText(taskItems.getAssignName());
            holder.assigneMailText.setText(taskItems.getAssignEmail());
            holder.assignContactText.setText(taskItems.getAssignContact());
            if (Utils.validateString(mSharedPreferences.getString(Utils.COMPANY_NAME, ""))) {
                holder.assignCompnayText.setVisibility(View.VISIBLE);
                holder.assignCompnayText.setText(mSharedPreferences.getString(Utils.COMPANY_NAME, ""));
            } else {
                holder.assignCompnayText.setVisibility(View.GONE);
            }


            holder.assignContactLayout.setTag(position);
            holder.assignContactLayout.setOnClickListener(phoneClick);
        }

        if (TASK_VIEW_TYPE == TASK_TIMING_VIEW) {
            holder.startDateText.setText(Utils.parseDateFrom(taskItems.getStartDate()));
            holder.startTimeText.setText(Utils.parseTime(taskItems.getStartTime()));
            holder.dueDateText.setText(Utils.parseDateFrom(taskItems.getDueDate()));
            holder.dueTimeText.setText(Utils.parseTime(taskItems.getDueTime()));

            String status = taskItems.getStatus();
            if (status.equals(Utils.START_WORK) || status.equals(Utils.COMPLETE)) {
                holder.imgEditDueDate.setVisibility(View.GONE);
                holder.imgEditDueDate.setTag(position);
                holder.imgEditDueDate.setOnClickListener(updateDueDateTimeClick);
            } else {
                holder.imgEditDueDate.setVisibility(View.GONE);
                holder.imgEditDueDate.setTag(position);
                holder.imgEditDueDate.setOnClickListener(updateDueDateTimeClick);
            }

        }

        if (TASK_VIEW_TYPE == TASK_ADDRESS_VIEW) {

            String street = "", city = "", state = "", country = "", pincode = "";
            if (Utils.validateString(taskItems.getStreet())) {
                street = taskItems.getStreet() + ", ";
            }
            if (Utils.validateString(taskItems.getCity())) {
                city = taskItems.getCity() + ", ";
            }
            if (Utils.validateString(taskItems.getState())) {
                state = taskItems.getState() + ", ";
            }
            if (Utils.validateString(taskItems.getCountry())) {
                country = taskItems.getCountry() + ", ";
            }
            if (Utils.validateString(taskItems.getPincode())) {
                pincode = taskItems.getPincode();
            }
            holder.addressText.setText(street + city + state + country + pincode);

            if (Utils.validateString(taskItems.getLatitude())) {
                latitude = Double.parseDouble(taskItems.getLatitude());
            }

            if (Utils.validateString(taskItems.getLongitude())) {
                longitude = Double.parseDouble(taskItems.getLongitude());
            }

            if (latitude != 0 && longitude != 0) {
                location = new LatLng(latitude, longitude);
                if (googleMap == null) {
                    SupportMapFragment mapFragment = SupportMapFragment.newInstance();
                    ((ProjectDetailsActivity) context).getSupportFragmentManager().beginTransaction().add(R.id.mapDetails, mapFragment, "MapFragment").commit();
                    mapFragment.getMapAsync(this);
                }
            }
        }

        if (TASK_VIEW_TYPE == TASK_ADDITIONAL_CONTACT_VIEW) {
            holder.headerText.setText(taskItems.getHeaderName());

            holder.addItemView.setTag(position);
            holder.addItemView.setOnClickListener(addAdditionalContactClick);

            holder.txtNoAddressContact.setTag(position);
            holder.txtNoAddressContact.setOnClickListener(addAdditionalContactClick);

            String status = taskItems.getStatus();
            if (status.equals(Utils.START_WORK) || status.equals(Utils.COMPLETE)) {
                holder.addItemView.setVisibility(View.VISIBLE);
                additionalFlag = true;
                holder.txtNoAddressContact.setOnClickListener(addAdditionalContactClick);
            } else {
                holder.addItemView.setVisibility(View.GONE);
                additionalFlag = false;
                holder.txtNoAddressContact.setOnClickListener(null);
            }

            if (taskItems.getAdditionalContactModel().size() > 0) {
                holder.txtNoAddressContact.setVisibility(View.GONE);
                holder.additionalRecyclerView.setVisibility(View.VISIBLE);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                additionalContactListAdapter = new AdditionalContactListAdapter(context, taskItems.getAdditionalContactModel(),
                        editAdditionalContactClick, deleteAdditionalContactClick, additionalFlag);
                holder.additionalRecyclerView.setLayoutManager(mLayoutManager);
                holder.additionalRecyclerView.setAdapter(additionalContactListAdapter);
            } else {
                holder.txtNoAddressContact.setVisibility(View.VISIBLE);
                holder.additionalRecyclerView.setVisibility(View.GONE);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                additionalContactListAdapter = new AdditionalContactListAdapter(context, taskItems.getAdditionalContactModel(),
                        editAdditionalContactClick, deleteAdditionalContactClick, additionalFlag);
                holder.additionalRecyclerView.setLayoutManager(mLayoutManager);
                holder.additionalRecyclerView.setAdapter(additionalContactListAdapter);
            }

        }

        if (TASK_VIEW_TYPE == TASK_NOTE_VIEW) {
            holder.headerText.setText(taskItems.getHeaderName());

            holder.addItemView.setTag(position);
            holder.addItemView.setOnClickListener(addNoteClick);

            holder.txtNoNotes.setTag(position);
            holder.txtNoNotes.setOnClickListener(addNoteClick);

            String status = taskItems.getStatus();
            if (status.equals(Utils.START_WORK) || status.equals(Utils.COMPLETE)) {
                holder.addItemView.setVisibility(View.VISIBLE);
                noteFlag = true;
                holder.txtNoNotes.setOnClickListener(addNoteClick);
            } else {
                holder.addItemView.setVisibility(View.GONE);
                noteFlag = false;
                holder.txtNoNotes.setOnClickListener(null);
            }

            if (taskItems.getNotesModel().size() > 0) {
                holder.txtNoNotes.setVisibility(View.GONE);
                holder.notesRecyclerView.setVisibility(View.VISIBLE);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                noteListAdapter = new NoteListAdapter(context, taskItems.getNotesModel(), editNoteClick, deleteNoteClick, noteFlag);
                holder.notesRecyclerView.setLayoutManager(mLayoutManager);
                holder.notesRecyclerView.setAdapter(noteListAdapter);
                noteListAdapter.notifyDataSetChanged();
            } else {
                holder.txtNoNotes.setVisibility(View.VISIBLE);
                holder.notesRecyclerView.setVisibility(View.GONE);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                noteListAdapter = new NoteListAdapter(context, taskItems.getNotesModel(), editNoteClick, deleteNoteClick, noteFlag);
                holder.notesRecyclerView.setLayoutManager(mLayoutManager);
                holder.notesRecyclerView.setAdapter(noteListAdapter);
                noteListAdapter.notifyDataSetChanged();
            }

        }

        if (TASK_VIEW_TYPE == TASK_PROJECT_PHASE_VIEW) {
            holder.headerText.setText(taskItems.getHeaderName());

            holder.addItemView.setTag(position);
            holder.addItemView.setOnClickListener(addProjectClick);

            holder.txtNoProjectPhase.setTag(position);
            holder.txtNoProjectPhase.setOnClickListener(addProjectClick);

            String status = taskItems.getStatus();
            if (status.equals(Utils.START_WORK) || status.equals(Utils.COMPLETE)) {
                holder.addItemView.setVisibility(View.VISIBLE);
                projectFlag = true;
                holder.txtNoProjectPhase.setOnClickListener(addProjectClick);
            } else {
                holder.addItemView.setVisibility(View.GONE);
                projectFlag = false;
                holder.txtNoProjectPhase.setOnClickListener(null);
            }

            if (taskItems.getProjectPhaseModel().size() > 0) {
                holder.txtNoProjectPhase.setVisibility(View.GONE);
                holder.projectRecyclerView.setVisibility(View.VISIBLE);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                ProjectPhaseListAdapter adapter = new ProjectPhaseListAdapter(context, taskItems.getProjectPhaseModel(), onEditProjectPhaseListner, onDeleteProjectPhaseListner, projectFlag);
                holder.projectRecyclerView.setLayoutManager(mLayoutManager);
                holder.projectRecyclerView.setAdapter(adapter);
            } else {
                holder.txtNoProjectPhase.setVisibility(View.VISIBLE);
                holder.projectRecyclerView.setVisibility(View.GONE);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                ProjectPhaseListAdapter adapter = new ProjectPhaseListAdapter(context, taskItems.getProjectPhaseModel(), onEditProjectPhaseListner, onDeleteProjectPhaseListner, projectFlag);
                holder.projectRecyclerView.setLayoutManager(mLayoutManager);
                holder.projectRecyclerView.setAdapter(adapter);
            }

        }

        if (TASK_VIEW_TYPE == TASK_MEDIA_VIEW) {
            holder.headerText.setText(taskItems.getHeaderName());
            holder.subHeaderText.setText("Click image to edit photos or view document");

            holder.addItemView.setTag(position);
            holder.addItemView.setOnClickListener(addMediaClick);

            holder.txtNoMedia.setTag(position);
            holder.txtNoMedia.setOnClickListener(addMediaClick);

            String status = taskItems.getStatus();
            if (status.equals(Utils.START_WORK) || status.equals(Utils.COMPLETE)) {
                holder.addItemView.setVisibility(View.VISIBLE);
                holder.txtNoMedia.setOnClickListener(addMediaClick);
            } else {
                holder.addItemView.setVisibility(View.GONE);
                holder.txtNoMedia.setOnClickListener(null);
            }

            if (taskItems.getMediaModel().size() > 0) {
                holder.txtNoMedia.setVisibility(View.GONE);
                holder.mediaRecyclerView.setVisibility(View.VISIBLE);
                if (isTablet.equalsIgnoreCase(Utils.TABLET)) {
                    holder.mediaRecyclerView.setLayoutManager(new GridLayoutManager(context, 5));
                } else {
                    holder.mediaRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));
                }
                mediaAdapter = new MediaAdapter(context, taskItems.getMediaModel(), mediaClick, mediaLongClick, status);
                holder.mediaRecyclerView.setAdapter(mediaAdapter);
            } else {
                holder.txtNoMedia.setVisibility(View.VISIBLE);
                holder.mediaRecyclerView.setVisibility(View.GONE);
                if (isTablet.equalsIgnoreCase(Utils.TABLET)) {
                    holder.mediaRecyclerView.setLayoutManager(new GridLayoutManager(context, 5));
                } else {
                    holder.mediaRecyclerView.setLayoutManager(new GridLayoutManager(context, 3));
                }
                mediaAdapter = new MediaAdapter(context, taskItems.getMediaModel(), mediaClick, mediaLongClick, status);
                holder.mediaRecyclerView.setAdapter(mediaAdapter);
            }

        }

        if (TASK_VIEW_TYPE == TASK_STATUS_BTN_VIEW) {
            String status = taskItems.getStatus();
            if (status.equalsIgnoreCase(Utils.START_WORK) || status.equalsIgnoreCase(Utils.COMPLETE))
                holder.updateStatusBtn.setVisibility(View.VISIBLE);
            else
                holder.updateStatusBtn.setVisibility(View.GONE);

            holder.updateStatusBtn.setText(status);
            holder.updateStatusBtn.setTag(position);
            holder.updateStatusBtn.setOnClickListener(updateStatusClick);
        }

        if (TASK_VIEW_TYPE == TASK_GENERATE_REPORT_BTN_VIEW) {
            String status = taskItems.getStatus();
            if (status.equalsIgnoreCase(Utils.START_WORK))
                holder.updateStatusBtn.setText(Utils.GENERATE_PROPOSAL);
            else
                holder.updateStatusBtn.setText(Utils.GENERATE_REPORT);
            holder.updateStatusBtn.setTag(position);
            holder.updateStatusBtn.setOnClickListener(generateReportClick);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtRogiUrl;

        //TODO:TASK_TITLE_VIEW..........
        TextView taskTitleText, taskDescriptionText;
        Button priorityBTN;

        //TODO: ASSIGN_CONTACT.......
        TextView assignNameText, assigneMailText, assignContactText, assignCompnayText;
        LinearLayout assignContactLayout;

        //TODO:TASK_TIMING_VIEW...........
        TextView startDateText, startTimeText, dueDateText, dueTimeText;

        //TODO:ADDRESS_VIEW........
        TextView addressText;

        //TODO: Header titleView....
        TextView headerText, subHeaderText;

        //TODO: Additional Contact....
        RecyclerView additionalRecyclerView;
        TextView txtNoAddressContact;

        //TODO: PROJECT_VIEW.....
        RecyclerView projectRecyclerView;
        TextView txtNoProjectPhase;

        //TODO:NOTE_VIEW......
        RecyclerView notesRecyclerView;
        TextView noteTime, noteText, updateStatusBtn, txtNoNotes;
        ImageView addItemView, editNoteBTN, deleteNoteBTN, editProjectPhaseBTN, deleteProjectPhaseBTN, imgEditDueDate;

        //TODO:MEDIA_VIEW....
        RecyclerView mediaRecyclerView;
        TextView txtNoMedia;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setTag(getAdapterPosition());

            txtRogiUrl = (TextView) itemView.findViewById(R.id.txtRogiUrl);

            taskTitleText = (TextView) itemView.findViewById(R.id.taskTitleText);
            taskDescriptionText = (TextView) itemView.findViewById(R.id.taskDescriptionText);
            priorityBTN = (Button) itemView.findViewById(R.id.priorityBTN);

            assignContactLayout = (LinearLayout) itemView.findViewById(R.id.assignContactLayout);
            assignNameText = (TextView) itemView.findViewById(R.id.assignNameText);
            assigneMailText = (TextView) itemView.findViewById(R.id.assigneMailText);
            assignContactText = (TextView) itemView.findViewById(R.id.assignContactText);
            assignCompnayText = (TextView) itemView.findViewById(R.id.assignCompnayText);

            startDateText = (TextView) itemView.findViewById(R.id.startDateText);
            startTimeText = (TextView) itemView.findViewById(R.id.startTimeText);
            dueDateText = (TextView) itemView.findViewById(R.id.dueDateText);
            dueTimeText = (TextView) itemView.findViewById(R.id.dueTimeText);
            imgEditDueDate = (ImageView) itemView.findViewById(R.id.imgEditDueDate);

            addressText = (TextView) itemView.findViewById(R.id.addressText);

            headerText = (TextView) itemView.findViewById(R.id.headerText);
            subHeaderText = (TextView) itemView.findViewById(R.id.subHeaderText);

            notesRecyclerView = (RecyclerView) itemView.findViewById(R.id.notesRecyclerView);
            noteTime = (TextView) itemView.findViewById(R.id.noteTime);
            noteText = (TextView) itemView.findViewById(R.id.noteText);
            addItemView = (ImageView) itemView.findViewById(R.id.addItemView);
            editNoteBTN = (ImageView) itemView.findViewById(R.id.editNoteBTN);
            deleteNoteBTN = (ImageView) itemView.findViewById(R.id.deleteNoteBTN);
            txtNoNotes = (TextView) itemView.findViewById(R.id.txtNoNotes);

            editProjectPhaseBTN = (ImageView) itemView.findViewById(R.id.editProjectPhaseBTN);
            deleteProjectPhaseBTN = (ImageView) itemView.findViewById(R.id.deleteProjectPhaseBTN);

            projectRecyclerView = (RecyclerView) itemView.findViewById(R.id.projectRecyclerView);
            txtNoProjectPhase = (TextView) itemView.findViewById(R.id.txtNoProjectPhase);

            additionalRecyclerView = (RecyclerView) itemView.findViewById(R.id.additionalRecyclerView);
            txtNoAddressContact = (TextView) itemView.findViewById(R.id.txtNoAddressContact);

            mediaRecyclerView = (RecyclerView) itemView.findViewById(R.id.mediaRecyclerView);
            txtNoMedia = (TextView) itemView.findViewById(R.id.txtNoMedia);

            updateStatusBtn = (TextView) itemView.findViewById(R.id.updateStatusBtn);
        }
    }

    @Override
    public void onMapReady(GoogleMap mMap) {
        googleMap = mMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.addMarker(new MarkerOptions().position(location));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        if (!isTablet.equalsIgnoreCase(Utils.TABLET))
            googleMap.moveCamera(CameraUpdateFactory.zoomTo(11.0f));

    }

}
