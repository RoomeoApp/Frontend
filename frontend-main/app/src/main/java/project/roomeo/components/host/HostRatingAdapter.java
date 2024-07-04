package project.roomeo.components.host;

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
import project.roomeo.components.admin.RatingViewHolder;
import project.roomeo.models.Rating;
import project.roomeo.models.Report;
import project.roomeo.service.ServiceUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HostRatingAdapter  extends RecyclerView.Adapter<RatingViewHolder> {

    private List<Rating> ratingList;
    private FragmentManager fragmentManager;

    public HostRatingAdapter(List<Rating> ratingList, FragmentManager fragmentManager) {
        this.ratingList = ratingList;
        this.fragmentManager = fragmentManager;
    }

    public HostRatingAdapter(List<Rating> ratingList) {
        this.ratingList = ratingList;
    }

    @NonNull
    @Override
    public RatingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.host_ratings_item, parent, false);
        return new RatingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingViewHolder holder, int position) {
        Rating request = ratingList.get(position);

        holder.bindData(request);

        holder.acceptButton.setOnClickListener(view -> {
            Report report = new Report(request.getId().intValue(),null,null);

            Call<Report> call = ServiceUtils.reportService.addReport(report);
            call.enqueue(new Callback<Report>() {
                @Override
                public void onResponse(@NonNull Call<Report> call, @NonNull Response<Report> response) {
                    if (response.isSuccessful()) {
                        HostRatingsFragment fragment = new HostRatingsFragment();
                        ((HostMainActivity) view.getContext()).loadFragment(fragment);
                        Toast.makeText(view.getContext(), "Report sent", Toast.LENGTH_SHORT).show();
                    } else {
                        onFailure(call, new Throwable("API call failed with status code: " + response.code()));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Report> call, @NonNull Throwable t) {
                    Log.e("Report", "API call failed: " + t.getMessage());

                }
            });
        });

        holder.declineButton.setOnClickListener(view -> {
            int guestId = request.getGuestId();
            Report report = new Report(null,null,guestId);

            Call<Report> call = ServiceUtils.reportService.addReport(report);
            call.enqueue(new Callback<Report>() {
                @Override
                public void onResponse(@NonNull Call<Report> call, @NonNull Response<Report> response) {
                    if (response.isSuccessful()) {
                        HostRatingsFragment fragment = new HostRatingsFragment();
                        ((HostMainActivity) view.getContext()).loadFragment(fragment);
                        Toast.makeText(view.getContext(), "Report sent", Toast.LENGTH_SHORT).show();
                    } else {
                        onFailure(call, new Throwable("API call failed with status code: " + response.code()));
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Report> call, @NonNull Throwable t) {
                    Log.e("Report", "API call failed: " + t.getMessage());

                }
            });
        });
    }


    @Override
    public int getItemCount() {
        return ratingList != null ? ratingList.size() : 0;
    }

}
