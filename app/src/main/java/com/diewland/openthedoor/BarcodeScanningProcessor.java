// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.diewland.openthedoor;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.diewland.openthedoor.helper.BarcodeGraphic;
import com.google.android.gms.common.api.Response;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.diewland.openthedoor.helper.FrameMetadata;
import com.diewland.openthedoor.helper.GraphicOverlay;
import com.diewland.openthedoor.helper.VisionProcessorBase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.List;

/** Barcode Detector Demo. */
public class BarcodeScanningProcessor extends VisionProcessorBase<List<FirebaseVisionBarcode>> {

  private static final String TAG = "BarcodeProcessor";

  private final FirebaseVisionBarcodeDetector detector;
  private Context ctx;
  private String decode;

  public BarcodeScanningProcessor(Context ctx) {
    // Note that if you know which format of barcode your app is dealing with, detection will be
    // faster to specify the supported barcode formats one by one, e.g.
    // new FirebaseVisionBarcodeDetectorOptions.Builder()
    //     .setBarcodeFormats(irebaseVisionBarcode.FORMAT_QR_CODE)
    //     .build();
    detector = FirebaseVision.getInstance().getVisionBarcodeDetector();
    ctx = ctx;
  }

  @Override
  public void stop() {
    try {
      detector.close();
    } catch (IOException e) {
      Log.e(TAG, "Exception thrown while trying to close Barcode Detector: " + e);
    }
  }

  @Override
  protected Task<List<FirebaseVisionBarcode>> detectInImage(FirebaseVisionImage image) {
    return detector.detectInImage(image);
  }

  @Override
  protected void onSuccess(
      @NonNull List<FirebaseVisionBarcode> barcodes,
      @NonNull FrameMetadata frameMetadata,
      @NonNull GraphicOverlay graphicOverlay) {
    graphicOverlay.clear();
    for (int i = 0; i < barcodes.size(); ++i) {
      FirebaseVisionBarcode barcode = barcodes.get(i);
      BarcodeGraphic barcodeGraphic = new BarcodeGraphic(graphicOverlay, barcode);
      graphicOverlay.add(barcodeGraphic);

      decode = barcode.getRawValue();
      Log.d(TAG, decode);

      Activity this_act = (Activity) ctx;
        Thread thread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    String url_str = "http:10.4.202.76:5000/verify?t=" + decode;
                    Log.d(TAG, url_str);
                    new URL(url_str).openStream();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }
  }

  @Override
  protected void onFailure(@NonNull Exception e) {
    Log.e(TAG, "Barcode detection failed " + e);
  }

}
