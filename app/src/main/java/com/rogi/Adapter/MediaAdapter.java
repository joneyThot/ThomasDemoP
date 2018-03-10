package com.rogi.Adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.rogi.Model.MediaModel;
import com.rogi.R;
import com.rogi.View.CustomVolleyRequest;
import com.rogi.View.Utils;

import java.io.File;
import java.util.List;


public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.ViewHolder> {
    private LayoutInflater inflater;
    private Context context;
    List<MediaModel> mediaDatas;
    private ImageLoader imageLoader;
    OnMediaClickListner mediaClick;
    OnMediaLongClickListner mediaLongClick;
    String status;
    boolean longPressFlag = false;


    public MediaAdapter(Context context, List<MediaModel> mediaDatas, OnMediaClickListner mediaClick,
                        OnMediaLongClickListner mediaLongClick, String status) {
        super();
        this.context = context;
        this.mediaDatas = mediaDatas;
        this.mediaClick = mediaClick;
        this.mediaLongClick = mediaLongClick;
        this.status = status;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context);
        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        View v = inflater.inflate(R.layout.activity_task_media_item_view, parent, false);
        RecyclerView.ViewHolder holder = new ViewHolder(v);
        return (ViewHolder) holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final MediaModel mainData = mediaDatas.get(position);
        String mediaId = mainData.getMediaId();
        String imageURL = mainData.getMedia();
        String videoURL = mainData.getVideoThumbImage();
        String docThumURL = mainData.getDocThumbImage();

        if (mediaDatas.get(position).isSelected()) {
            holder.imgCheck.setVisibility(View.VISIBLE);
        }

        File file;
        if (mainData.getMediaType().equalsIgnoreCase("image")) {
            file = new File(Environment.getExternalStorageDirectory() + "/Media/Image/", mediaId + ".jpg");
            if (file.exists()) {
                String IMAGE_URL = Environment.getExternalStorageDirectory() + "/Media/Image/" + mediaId + ".jpg";
                holder.mediaView.setVisibility(View.GONE);
                holder.mediaSdcardView.setVisibility(View.VISIBLE);
                holder.mediaAudioView.setVisibility(View.GONE);
                holder.mediaSdcardView.setImageURI(Uri.parse(IMAGE_URL));
                holder.imgVideo.setVisibility(View.GONE);
            } else {
                if (!imageURL.isEmpty()) {
                    String IMAGE_URL = imageURL;
                    imageLoader.get(IMAGE_URL, ImageLoader.getImageListener(holder.mediaView, R.mipmap.default_image,
                            R.mipmap.default_image));
                    holder.mediaView.setImageUrl(IMAGE_URL, imageLoader);
                    holder.mediaView.setVisibility(View.VISIBLE);
                    holder.mediaSdcardView.setVisibility(View.GONE);
                    holder.mediaAudioView.setVisibility(View.GONE);
                    holder.imgVideo.setVisibility(View.GONE);
                }
            }
        } else if (mainData.getMediaType().equalsIgnoreCase("video")) {
            file = new File(Environment.getExternalStorageDirectory() + "/Media/VideoThumb/", mediaId + ".jpg");
            if (file.exists()) {
                String VIDEO_URL = Environment.getExternalStorageDirectory() + "/Media/VideoThumb/" + mediaId + ".jpg";
                holder.mediaView.setVisibility(View.GONE);
                holder.mediaSdcardView.setVisibility(View.VISIBLE);
                holder.mediaAudioView.setVisibility(View.GONE);
                holder.mediaSdcardView.setImageURI(Uri.parse(VIDEO_URL));
                holder.imgVideo.setVisibility(View.VISIBLE);
            } else {
                if (!videoURL.isEmpty()) {
                    String VIDEO_URL = videoURL;
                    imageLoader.get(VIDEO_URL, ImageLoader.getImageListener(holder.mediaView, R.mipmap.default_image,
                            R.mipmap.default_image));
                    holder.mediaView.setImageUrl(VIDEO_URL, imageLoader);
                    holder.mediaView.setVisibility(View.VISIBLE);
                    holder.mediaSdcardView.setVisibility(View.GONE);
                    holder.mediaAudioView.setVisibility(View.GONE);
                    holder.imgVideo.setVisibility(View.VISIBLE);
                } else {
                    holder.mediaSdcardView.setVisibility(View.VISIBLE);
                    holder.mediaAudioView.setVisibility(View.GONE);
                    holder.imgVideo.setVisibility(View.VISIBLE);
                }
            }
        }
        if (mainData.getMediaType().equalsIgnoreCase("doc")) {
            /*file = new File(Environment.getExternalStorageDirectory() + "/Media/VideoThumb/", mediaId + ".jpg");
            if (file.exists()) {
//                String IMAGE_URL = Environment.getExternalStorageDirectory() + "/Media/VideoThumb/" + mediaId + ".jpg";
                holder.mediaView.setVisibility(View.GONE);
                holder.mediaSdcardView.setImageResource(R.mipmap.msword_thumb);
                holder.mediaSdcardView.setVisibility(View.VISIBLE);
                holder.mediaAudioView.setVisibility(View.GONE);
//                holder.mediaSdcardView.setImageURI(Uri.parse(IMAGE_URL));
                holder.imgVideo.setVisibility(View.GONE);
            } else {*/
//                if (!docThumURL.isEmpty()) {
//                    String IMAGE_URL = docThumURL;
//                    imageLoader.get(IMAGE_URL, ImageLoader.getImageListener(holder.mediaView, R.mipmap.default_image,
//                            R.mipmap.default_image));
//                    holder.mediaView.setImageUrl(IMAGE_URL, imageLoader);
//                    holder.mediaView.setImageResource(R.mipmap.msword_thumb);
            holder.mediaView.setVisibility(View.GONE);
            holder.mediaSdcardView.setImageResource(R.mipmap.msword_thumb);
            holder.mediaSdcardView.setVisibility(View.VISIBLE);
            holder.mediaAudioView.setVisibility(View.GONE);
            holder.imgVideo.setVisibility(View.GONE);
//                }
//            }
        }
        if (mainData.getMediaType().equalsIgnoreCase("txt")) {
            /*file = new File(Environment.getExternalStorageDirectory() + "/Media/VideoThumb/", mediaId + ".jpg");
            if (file.exists()) {
//                String IMAGE_URL = Environment.getExternalStorageDirectory() + "/Media/VideoThumb/" + mediaId + ".jpg";
                holder.mediaView.setVisibility(View.GONE);
                holder.mediaSdcardView.setVisibility(View.VISIBLE);
                holder.mediaSdcardView.setImageResource(R.mipmap.text_thumb);
                holder.mediaAudioView.setVisibility(View.GONE);
//                holder.mediaSdcardView.setImageURI(Uri.parse(IMAGE_URL));
                holder.imgVideo.setVisibility(View.GONE);
            } else {*/
//                if (!docThumURL.isEmpty()) {
//                    String IMAGE_URL = docThumURL;
//                    imageLoader.get(IMAGE_URL, ImageLoader.getImageListener(holder.mediaView, R.mipmap.default_image,
//                            R.mipmap.default_image));
//                    holder.mediaView.setImageUrl(IMAGE_URL, imageLoader);
//                    holder.mediaView.setImageResource(R.mipmap.text_thumb);
            holder.mediaView.setVisibility(View.GONE);
            holder.mediaSdcardView.setVisibility(View.VISIBLE);
            holder.mediaSdcardView.setImageResource(R.mipmap.text_thumb);
            holder.mediaAudioView.setVisibility(View.GONE);
            holder.imgVideo.setVisibility(View.GONE);
//                }
//            }
        }
        if (mainData.getMediaType().equalsIgnoreCase("docx")) {
            /*file = new File(Environment.getExternalStorageDirectory() + "/Media/VideoThumb/", mediaId + ".jpg");
            if (file.exists()) {
//                String IMAGE_URL = Environment.getExternalStorageDirectory() + "/Media/VideoThumb/" + mediaId + ".jpg";
                holder.mediaView.setVisibility(View.GONE);
                holder.mediaSdcardView.setVisibility(View.VISIBLE);
                holder.mediaSdcardView.setImageResource(R.mipmap.msword_thumb);
                holder.mediaAudioView.setVisibility(View.GONE);
//                holder.mediaSdcardView.setImageURI(Uri.parse(IMAGE_URL));
                holder.imgVideo.setVisibility(View.GONE);
            } else {*/
//                if (!docThumURL.isEmpty()) {
//                    String IMAGE_URL = docThumURL;
//                    imageLoader.get(IMAGE_URL, ImageLoader.getImageListener(holder.mediaView, R.mipmap.default_image,
//                            R.mipmap.default_image));
//                    holder.mediaView.setImageUrl(IMAGE_URL, imageLoader);
//                    holder.mediaView.setImageResource(R.mipmap.msword_thumb);
            holder.mediaView.setVisibility(View.GONE);
            holder.mediaSdcardView.setVisibility(View.VISIBLE);
            holder.mediaSdcardView.setImageResource(R.mipmap.msword_thumb);
            holder.mediaAudioView.setVisibility(View.GONE);
            holder.imgVideo.setVisibility(View.GONE);
//                }
//            }
        }
        if (mainData.getMediaType().equalsIgnoreCase("pdf")) {
            /*file = new File(Environment.getExternalStorageDirectory() + "/Media/VideoThumb/", mediaId + ".jpg");
            if (file.exists()) {
//                String IMAGE_URL = Environment.getExternalStorageDirectory() + "/Media/VideoThumb/" + mediaId + ".jpg";
                holder.mediaView.setVisibility(View.GONE);
                holder.mediaSdcardView.setVisibility(View.VISIBLE);
                holder.mediaSdcardView.setImageResource(R.mipmap.pdf_thumb);
                holder.mediaAudioView.setVisibility(View.GONE);
//                holder.mediaSdcardView.setImageURI(Uri.parse(IMAGE_URL));
                holder.imgVideo.setVisibility(View.GONE);
            } else {*/
//                if (!docThumURL.isEmpty()) {
//                    String IMAGE_URL = docThumURL;
//                    imageLoader.get(IMAGE_URL, ImageLoader.getImageListener(holder.mediaView, R.mipmap.default_image,
//                            R.mipmap.default_image));
//                    holder.mediaView.setImageUrl(IMAGE_URL, imageLoader);
//                    holder.mediaView.setImageResource(R.mipmap.pdf_thumb);
            holder.mediaView.setVisibility(View.GONE);
            holder.mediaSdcardView.setVisibility(View.VISIBLE);
            holder.mediaSdcardView.setImageResource(R.mipmap.pdf_thumb);
            holder.mediaAudioView.setVisibility(View.GONE);
            holder.imgVideo.setVisibility(View.GONE);
//                }
//            }
        } else if (mainData.getMediaType().equalsIgnoreCase("audio")) {
            file = new File(Environment.getExternalStorageDirectory() + "/Media/Audio/", mediaId + ".wav");
            if (file.exists()) {
                String IMAGE_URL = Environment.getExternalStorageDirectory() + "/Media/Audio/" + mediaId + ".wav";
                holder.mediaView.setVisibility(View.GONE);
                holder.mediaSdcardView.setVisibility(View.GONE);
                holder.mediaAudioView.setVisibility(View.VISIBLE);
                holder.mediaSdcardView.setImageURI(Uri.parse(IMAGE_URL));
                holder.imgVideo.setVisibility(View.VISIBLE);
            } else {
                if (!imageURL.isEmpty()) {
                    holder.mediaView.setVisibility(View.GONE);
                    holder.mediaAudioView.setVisibility(View.VISIBLE);
                    holder.mediaSdcardView.setVisibility(View.GONE);
                    holder.imgVideo.setVisibility(View.VISIBLE);
                }
            }
        }

        holder.mediaView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mediaDatas.size(); i++) {
                    if (mediaDatas.get(i).isSelected()) {
                        longPressFlag = true;
                        break;
                    } else {
                        longPressFlag = false;
                    }
                }
                if (mainData.isSelected() && longPressFlag) {
                    mainData.setSelected(false);
                    holder.imgCheck.setVisibility(View.GONE);
                } else {
                    mainData.setSelected(true);
                    holder.imgCheck.setVisibility(View.VISIBLE);
                }
                if (!longPressFlag) {
                    mediaClick.onItemClick(mainData, longPressFlag);
                    mainData.setSelected(false);
                    holder.imgCheck.setVisibility(View.GONE);
                } else {
                    mediaClick.onItemClick(mainData, longPressFlag);
                }

            }
        });

        holder.mediaSdcardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mediaDatas.size(); i++) {
                    if (mediaDatas.get(i).isSelected()) {
                        longPressFlag = true;
                        break;
                    } else {
                        longPressFlag = false;
                    }
                }
                if (mainData.isSelected() && longPressFlag) {
                    mainData.setSelected(false);
                    holder.imgCheck.setVisibility(View.GONE);
                } else {
                    mainData.setSelected(true);
                    holder.imgCheck.setVisibility(View.VISIBLE);
                }
                if (!longPressFlag) {
                    mainData.setSelected(false);
                    holder.imgCheck.setVisibility(View.GONE);
                    mediaClick.onItemClick(mainData, longPressFlag);
                } else {
                    mediaClick.onItemClick(mainData, longPressFlag);
                }
            }
        });

        holder.mediaAudioView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mediaDatas.size(); i++) {
                    if (mediaDatas.get(i).isSelected()) {
                        longPressFlag = true;
                        break;
                    } else {
                        longPressFlag = false;
                    }
                }
                if (mainData.isSelected() && longPressFlag) {
                    mainData.setSelected(false);
                    holder.imgCheck.setVisibility(View.GONE);
                } else {
                    mainData.setSelected(true);
                    holder.imgCheck.setVisibility(View.VISIBLE);
                }
                if (!longPressFlag) {
                    mainData.setSelected(false);
                    holder.imgCheck.setVisibility(View.GONE);
                    mediaClick.onItemClick(mainData, longPressFlag);
                } else {
                    mediaClick.onItemClick(mainData, longPressFlag);
                }
            }
        });

        if (status.equalsIgnoreCase(Utils.START_WORK)) {
            holder.mediaView.setOnLongClickListener(null);
        } else {
            holder.mediaView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mainData.setSelected(true);
                    holder.imgCheck.setVisibility(View.VISIBLE);
                    mediaLongClick.onItemClick(mainData);
                    return true;
                }
            });
        }

        if (status.equalsIgnoreCase(Utils.START_WORK)) {
            holder.mediaSdcardView.setOnLongClickListener(null);
        } else {
            holder.mediaSdcardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mainData.setSelected(true);
                    holder.imgCheck.setVisibility(View.VISIBLE);
                    mediaLongClick.onItemClick(mainData);
                    return true;
                }
            });
        }

        if (status.equalsIgnoreCase(Utils.START_WORK)) {
            holder.mediaAudioView.setOnLongClickListener(null);
        } else {
            holder.mediaAudioView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mainData.setSelected(true);
                    holder.imgCheck.setVisibility(View.VISIBLE);
                    mediaLongClick.onItemClick(mainData);
                    return true;
                }
            });
        }


    }

    public interface OnMediaClickListner {
        void onItemClick(MediaModel data, boolean flag);
    }

    public interface OnMediaLongClickListner {
        void onItemClick(MediaModel data);
    }

    @Override
    public int getItemCount() {
        return mediaDatas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        NetworkImageView mediaView;
        ImageView imgVideo, mediaSdcardView, mediaAudioView, imgCheck;

        public ViewHolder(View itemView) {
            super(itemView);
            mediaView = (NetworkImageView) itemView.findViewById(R.id.mediaView);
            imgVideo = (ImageView) itemView.findViewById(R.id.imgVideo);
            mediaSdcardView = (ImageView) itemView.findViewById(R.id.mediaSdcardView);
            mediaAudioView = (ImageView) itemView.findViewById(R.id.mediaAudioView);
            imgCheck = (ImageView) itemView.findViewById(R.id.imgCheck);
        }
    }
}

