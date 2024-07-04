package project.roomeo.components.admin;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import project.roomeo.R;
import project.roomeo.models.Rating;
import project.roomeo.models.Report;
import project.roomeo.models.Reservation;
import project.roomeo.models.enums.ReservationRequestStatus;
import project.roomeo.service.ServiceUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public class UserReportRequestAdapter extends RecyclerView.Adapter<ReportViewHolder> {

    private List<Report> reportList;
    private FragmentManager fragmentManager;

    public UserReportRequestAdapter(List<Report> reportList, FragmentManager fragmentManager) {
        this.reportList = reportList;
        this.fragmentManager = fragmentManager;
    }

    public UserReportRequestAdapter(List<Report> reportList) {
        this.reportList = reportList;
    }

    @NonNull
    @Override
    public ReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_report_request_item, parent, false);
        return new ReportViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportViewHolder holder, int position) {
        Report request = reportList.get(position);

        holder.bindData(request);

        holder.acceptButton.setOnClickListener(view -> {
            int userId;
            if (request.getHostId()!=null){
                userId = request.getHostId();
            }else{
                userId = request.getGuestId();
            }

            Call<Void> call = ServiceUtils.reportService.blockUser(String.valueOf(userId));
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        Long reportId = request.getId();

                        Call<Void> call2 = ServiceUtils.reportService.deleteReport(reportId);
                        call2.enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                if (response.isSuccessful()) {
                                    UserReportRequestsFragment fragment = new UserReportRequestsFragment();
                                    ((AdminMainActivity) view.getContext()).loadFragment(fragment);

                                    Toast.makeText(view.getContext(), "Report request accepted.", Toast.LENGTH_SHORT).show();
                                } else {
                                    onFailure(call2, new Throwable("API call failed with status code: " + response.code()));
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                Log.e("RatingAdapter", "API call failed: " + t.getMessage());

                            }
                        });} else {
                        onFailure(call, new Throwable("API call failed with status code: " + response.code()));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.e("RatingAdapter", "API call failed: " + t.getMessage());

                }
            });

            if(request.getGuestId()!=null) {
                Call<List<Reservation>> call3 = ServiceUtils.reservationService.getGuestReservations(String.valueOf(userId));
                call3.enqueue(new Callback<List<Reservation>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Reservation>> call3, @NonNull Response<List<Reservation>> response) {
                        if (response.isSuccessful()) {
                            List<Reservation> list = response.body();
                            if (list != null) {
                                for (int i = 0; i < list.size(); i++) {
                                    Call<Void> call2 = ServiceUtils.reservationService.deleteReservation(list.get(i).getId());
                                    call2.enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                            if (response.isSuccessful()) {
//                                                UserReportRequestsFragment fragment = new UserReportRequestsFragment();
//                                                ((AdminMainActivity) view.getContext()).loadFragment(fragment);
                                            } else {
                                                onFailure(call2, new Throwable("API call failed with status code: " + response.code()));
                                            }
                                        }

                                        @Override
                                        public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                            Log.e("RatingAdapter", "API call failed: " + t.getMessage());

                                        }
                                    });
                                }

                            } else {
                                onFailure(call3, new Throwable("API call failed with status code: " + response.code()));
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Reservation>> call, @NonNull Throwable t) {
                        Log.e("Report", "API call failed: " + t.getMessage());
                    }
                });
            }
        });

        holder.declineButton.setOnClickListener(view -> {
            Long reportId = request.getId();

            Call<Void> call = ServiceUtils.reportService.deleteReport(reportId);
            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                    if (response.isSuccessful()) {
                        UserReportRequestsFragment fragment = new UserReportRequestsFragment();
                        ((AdminMainActivity) view.getContext()).loadFragment(fragment);

                        Toast.makeText(view.getContext(), "Report request rejected.", Toast.LENGTH_SHORT).show();
                    } else {
                        onFailure(call, new Throwable("API call failed with status code: " + response.code()));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                    Log.e("RatingAdapter", "API call failed: " + t.getMessage());

                }
            });
        });
    }


    @Override
    public int getItemCount() {
        return reportList != null ? reportList.size() : 0;
    }

}