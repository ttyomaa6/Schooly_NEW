package com.egormoroz.schooly;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.PixelCopy;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.egormoroz.schooly.ui.main.Shop.Clothes;
import com.egormoroz.schooly.ui.main.UserInformation;
import com.egormoroz.schooly.ui.news.NewsAdapter;
import com.egormoroz.schooly.ui.news.NewsItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class InstagramShareFragment extends Fragment {

    Fragment fragment;
    Clothes clothes;
    UserInformation userInformation;
    Bundle bundle;
    RelativeLayout relativeLayout,relativeBackground;
    ImageView clothesImage,back,imageBackground;
    TextView clothesTitle,clothesCreator,share;
    FirebaseModel firebaseModel=new FirebaseModel();
    String type;
    SurfaceView surfaceView;
    NewsItem newsItem;
    static FilamentModel filamentModel=new FilamentModel();
    Person person;
    static URI uri;
    static Future<Buffer> future;
    static Buffer buffer1;
    static byte[] buffer;
    String socialMediaType;
    CircularProgressIndicator progressIndicator;
    static int loadValue=0;

    public InstagramShareFragment(Fragment fragment, UserInformation userInformation, Bundle bundle, Clothes clothes, String type
            , NewsItem newsItem,Person person,String socialMediaType) {
        this.fragment = fragment;
        this.userInformation=userInformation;
        this.bundle=bundle;
        this.clothes=clothes;
        this.type=type;
        this.newsItem=newsItem;
        this.person=person;
        this.socialMediaType=socialMediaType;
    }

    public static InstagramShareFragment newInstance(Fragment fragment, UserInformation userInformation, Bundle bundle,Clothes clothes,String type,NewsItem newsItem,Person person
    ,String socialMediaType) {
        return new InstagramShareFragment(fragment,userInformation,bundle,clothes,type,newsItem,person,socialMediaType);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_instagram_share, container, false);
        BottomNavigationView bnv = getActivity().findViewById(R.id.bottomNavigationView);
        bnv.setVisibility(bnv.GONE);
        firebaseModel.initAll();
        return root;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        back=view.findViewById(R.id.back);
        relativeLayout=view.findViewById(R.id.relative);
        clothesCreator=view.findViewById(R.id.clothesCreator);
        clothesImage=view.findViewById(R.id.clothesImage);
        clothesTitle=view.findViewById(R.id.clothesTitle);
        share=view.findViewById(R.id.share);
        relativeBackground=view.findViewById(R.id.relativeBackground);
        surfaceView=view.findViewById(R.id.surfaceView);
        imageBackground=view.findViewById(R.id.imageBackground);
        progressIndicator=view.findViewById(R.id.progressIndicator);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loadValue==0)
                RecentMethods.setCurrentFragment(fragment, getActivity());
            }
        });

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (loadValue==0)
                RecentMethods.setCurrentFragment(fragment, getActivity());
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);

        if(type.equals("look")){
            relativeLayout.setVisibility(View.GONE);
            imageBackground.setVisibility(View.GONE);
            loadValue=1;
            LoadNewsItemInScene loadNewsItemInScene=new LoadNewsItemInScene(userInformation, newsItem, new Callbacks.loadNewsTread() {
                @Override
                public void LoadNews(NewsItem newsItem)  {
                    progressIndicator.setVisibility(View.GONE);
                    try {
                        filamentModel.initShareFilament(surfaceView,newsItem.getPerson().getBody().getBuffer(),true,null,"regularRender",true);
                        loadClothesInScene(newsItem.getClothesCreators());
                        if(newsItem.getPerson().getBody().getColorX()!=null){
                            com.egormoroz.schooly.Color color=new com.egormoroz.schooly.Color();
                            color.setColorX(newsItem.getPerson().getBody().getColorX());
                            color.setColorY(newsItem.getPerson().getBody().getColorY());
                            color.setColorZ(newsItem.getPerson().getBody().getColorZ());
                            filamentModel.changeColor(newsItem.getPerson().getBody().getPartType(),color );
                        }
                        loadClothesInScene(newsItem.getClothesCreators());
                        if(newsItem.getPerson().getBrows()!=null){
                            filamentModel.populateSceneFacePart(newsItem.getPerson().getBrows().getBuffer());
                            if(newsItem.getPerson().getBrows().getColorX()!=null){
                                com.egormoroz.schooly.Color color=new com.egormoroz.schooly.Color();
                                color.setColorX(newsItem.getPerson().getBrows().getColorX());
                                color.setColorY(newsItem.getPerson().getBrows().getColorY());
                                color.setColorZ(newsItem.getPerson().getBrows().getColorZ());
                                filamentModel.changeColor(newsItem.getPerson().getBrows().getPartType(),color );
                            }
                        }
                        if(newsItem.getPerson().getHair()!=null){
                            filamentModel.populateSceneFacePart(newsItem.getPerson().getHair().getBuffer());
                            if(newsItem.getPerson().getHair().getColorX()!=null){
                                com.egormoroz.schooly.Color color=new com.egormoroz.schooly.Color();
                                color.setColorX(newsItem.getPerson().getHair().getColorX());
                                color.setColorY(newsItem.getPerson().getHair().getColorY());
                                color.setColorZ(newsItem.getPerson().getHair().getColorZ());
                                filamentModel.changeColor(newsItem.getPerson().getHair().getPartType(),color );
                            }

                        }
                        if(newsItem.getPerson().getLips().getBuffer()!=null){
                            filamentModel.populateSceneFacePart(newsItem.getPerson().getLips().getBuffer());
                            if(newsItem.getPerson().getLips().getColorX()!=null){
                                com.egormoroz.schooly.Color color=new com.egormoroz.schooly.Color();
                                color.setColorX(newsItem.getPerson().getLips().getColorX());
                                color.setColorY(newsItem.getPerson().getLips().getColorY());
                                color.setColorZ(newsItem.getPerson().getLips().getColorZ());
                                filamentModel.changeColor(newsItem.getPerson().getLips().getPartType(),color );
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    loadValue=0;
                }
            });
                share.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onClick(View v) {
                        if (loadValue==0) {
                            if (socialMediaType.equals("instagram")) {
                                getBitmapFormSurfaceView(surfaceView, getActivity(), new NewsAdapter.Callback<Bitmap>() {
                                    @Override
                                    public void onResult1(Bitmap bitmap) {
                                        Uri backgroundAssetUri = getImageUri(getActivity(), bitmap);
                                        String sourceApplication = "com.egormoroz.schooly";

                                        Intent intent = new Intent("com.instagram.share.ADD_TO_STORY");
                                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                                        intent.setDataAndType(backgroundAssetUri, "image/*");


                                        Activity activity = getActivity();
                                        if (activity.getPackageManager().resolveActivity(intent, 0) != null) {
                                            activity.startActivityForResult(intent, 0);
                                        }
                                    }
                                });
                            } else if (socialMediaType.equals("telegram")) {
                                getBitmapFormSurfaceView(surfaceView, getActivity(), new NewsAdapter.Callback<Bitmap>() {
                                    @Override
                                    public void onResult1(Bitmap bitmap) {
                                        String TelegramName = "org.telegram.messenger";
                                        Intent shareIntent = new Intent();
                                        shareIntent.setAction(Intent.ACTION_SEND);
                                        shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(getActivity(), bitmap));
                                        shareIntent.setType("image/png");
                                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        shareIntent.setPackage(TelegramName);
                                        startActivity(Intent.createChooser(shareIntent, null));
                                    }
                                });
                            } else if (socialMediaType.equals("all")) {
                                getBitmapFormSurfaceView(surfaceView, getActivity(), new NewsAdapter.Callback<Bitmap>() {
                                    @Override
                                    public void onResult1(Bitmap bitmap) {
                                        Intent sendIntent = new Intent();
                                        sendIntent.setAction(Intent.ACTION_SEND);
                                        sendIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(getActivity(), bitmap));
                                        sendIntent.setType("image/png");
                                        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        Intent shareIntent = Intent.createChooser(sendIntent, null);
                                        startActivity(shareIntent);
                                    }
                                });
                            }
                        }
                    }
                });
        }else{
            surfaceView.setVisibility(View.GONE);
            progressIndicator.setVisibility(View.GONE);
            Picasso.get().load(clothes.getClothesImage()).into(clothesImage);
            clothesTitle.setText(clothes.getClothesTitle());
            clothesCreator.setText(clothes.getCreator());

            share.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onClick(View v) {
                    if(socialMediaType.equals("instagram")){
                        getBitmapFormView(relativeBackground, getActivity(), new NewsAdapter.Callback<Bitmap>() {
                            @Override
                            public void onResult1(Bitmap bitmap) {
                                Uri backgroundAssetUri = getImageUri(getActivity(), bitmap);
                                String sourceApplication = "com.egormoroz.schooly";


                                Intent intent = new Intent("com.instagram.share.ADD_TO_STORY");

                                intent.setDataAndType(backgroundAssetUri, "image/*");
                                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


                                Activity activity = getActivity();
                                if (activity.getPackageManager().resolveActivity(intent, 0) != null) {
                                    activity.startActivityForResult(intent, 0);
                                }
                            }
                        });
                    }else if(socialMediaType.equals("telegram")){
                        getBitmapFormView(relativeBackground, getActivity(), new NewsAdapter.Callback<Bitmap>() {
                            @Override
                            public void onResult1(Bitmap bitmap) {
                                String TelegramName = "org.telegram.messenger";
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(getActivity(),bitmap));
                                shareIntent.setType("image/png");
                                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                shareIntent.setPackage(TelegramName);
                                Log.d("####", "a");
                                startActivity(Intent.createChooser(shareIntent, null));
                            }
                        });
                    }else if(socialMediaType.equals("all")){
                        getBitmapFormView(relativeBackground, getActivity(), new NewsAdapter.Callback<Bitmap>() {
                            @Override
                            public void onResult1(Bitmap bitmap) {
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_STREAM, getImageUri(getActivity(),bitmap));
                                sendIntent.setType("image/png");
                                sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                Intent shareIntent = Intent.createChooser(sendIntent, null);
                                startActivity(shareIntent);
                            }
                        });
                    }
                }
            });
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void getBitmapFormView(View view, Activity activity, NewsAdapter.Callback<Bitmap> callback) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

        int[] locations = new int[2];
        view.getLocationInWindow(locations);
        Rect rect = new Rect(locations[0], locations[1], locations[0] + view.getWidth(), locations[1] + view.getHeight());


        PixelCopy.request(activity.getWindow(), rect, bitmap, copyResult -> {
            if (copyResult == PixelCopy.SUCCESS) {
                callback.onResult1(bitmap);
            }
        }, new Handler(Looper.getMainLooper()));
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void getBitmapFormSurfaceView(View view, Activity activity, NewsAdapter.Callback<Bitmap> callback) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        PixelCopy.request((SurfaceView) view, bitmap, copyResult -> {
            if (copyResult == PixelCopy.SUCCESS) {
                callback.onResult1(bitmap);
            }
        }, new Handler(Looper.getMainLooper()));
    }


    public void loadClothesInScene(ArrayList<Clothes> clothesArrayList){
        for(int i=0;i<clothesArrayList.size();i++){
            Clothes clothes=clothesArrayList.get(i);
            if(clothes.getBuffer()!=null){
                filamentModel.populateScene(clothes.getBuffer(), clothes);
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String s=firebaseModel.getUsersReference().push().getKey();
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, s, null);
        return Uri.parse(path);
    }

    public static Buffer loadS(String model){
        try {
            uri = new URI(model);
            buffer = RecentMethods.getBytes(uri.toURL());
            buffer1= ByteBuffer.wrap(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return buffer1;
    }

    static class LongRunningTask implements Callable<Buffer> {
        private String model;

        public LongRunningTask(String model) {
            this.model = model;
        }

        @Override
        public Buffer call() {
            return loadS(model);
        }
    }

    public static class TaskRunner {
        private final Executor executor = Executors.newSingleThreadExecutor();
        private final Handler handler = new Handler(Looper.getMainLooper());

        public interface Callback<String> {
            void onComplete(Buffer result) throws IOException, URISyntaxException;
        }

        public <Buffer> void executeAsync(Callable<Buffer> callable, com.egormoroz.schooly.TaskRunner.Callback<Buffer> callback) {
            executor.execute(() -> {
                Buffer result = null;
                try {
                    result = callable.call();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Buffer finalResult = result;
                handler.post(() -> {
                    try {
                        callback.onComplete((Buffer) finalResult);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                });
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        filamentModel.postFrameCallback();
    }

    @Override
    public void onPause() {
        super.onPause();

        filamentModel.removeFrameCallback();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        filamentModel.removeFrameCallback();
    }
}
