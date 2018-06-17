using System;
using System.Collections;
using System.Collections.Generic;
using System.IO;
using UnityEngine;
using UnityEngine.UI;

public class Caller : MonoBehaviour
{

    float[] contentData;
    float[] styleData;
    int[] outimageData;

    int contentWidth;
    int contentHeight;
    int styleWidth;
    int styleHeight;

    public RawImage ContentImage;
    public RawImage StyleImage;
    public RawImage OutImage;

    public Button StartBtn;
    

    // Use this for initialization
    void Start()
    {
        StartBtn.onClick.AddListener(OnStartTransfer);


      
    }
    void OnStartTransfer()
    {

        StartCoroutine(DoTransfer());
    }

    // Update is called once per frame
    void Update()
    {

    }

    jvalue ConvertFloatArrayToJNI(float[] values)
    {

        IntPtr jAryPtr = AndroidJNIHelper.ConvertToJNIArray(values);
        jvalue jv = new jvalue();
        jv.l = jAryPtr;
        return jv;
    }
    AndroidJavaObject JavaArrayFromCS(float[] values)
    {
        AndroidJavaClass arrayClass = new AndroidJavaClass("java.lang.reflect.Array");
        AndroidJavaObject arrayObject = arrayClass.CallStatic<AndroidJavaObject>("newInstance", new AndroidJavaClass("java.lang.Float"), values.Length);
        for (int i = 0; i < values.Length; ++i)
        {
            arrayClass.CallStatic("set", arrayObject, i, new AndroidJavaObject("java.lang.Float", values[i]));
        }

        return arrayObject;
    }

    IEnumerator LoadContentTexture(string path)
    {
        WWW www = new WWW(path);
        if (!www.isDone)
        {
            yield return null;
        }
        if (string.IsNullOrEmpty(www.error))
        {
            Debug.Log("Load Succ");
            Texture2D tex = www.texture;

            Color[] colors = tex.GetPixels();

            float[] data = new float[tex.width * tex.height * 3];

            for (int i = 0; i < colors.Length; i++)
            {
                data[i * 3] = colors[i].r;
                data[i * 3 + 1] = colors[i].g;
                data[i * 3 + 2] = colors[i].b;
            }
            contentData = data;
            contentWidth = tex.width;
            contentHeight = tex.height;

            ContentImage.texture = tex;
        }
        else
        {

            Debug.Log("Load Failed");
        }

    }

    IEnumerator LoadStyleTexture(string path)
    {
        WWW www = new WWW(path);
        if (!www.isDone)
        {
            yield return null;
        }
        if (string.IsNullOrEmpty(www.error))
        {
            Debug.Log("Load Succ");
            Texture2D tex = www.texture;

            Color[] colors = tex.GetPixels();

            float[] data = new float[tex.width * tex.height * 3];

            for (int i = 0; i < colors.Length; i++)
            {
                data[i * 3] = colors[i].r;
                data[i * 3 + 1] = colors[i].g;
                data[i * 3 + 2] = colors[i].b;
            }
            styleData = data;
            styleWidth = tex.width;
            styleHeight = tex.height;

            StyleImage.texture = tex;
        }
        else
        {

            Debug.Log("Load Failed");
        }

    }

    IEnumerator DoTransfer()
    {
        string contentPath = Application.streamingAssetsPath + "/lances.jpg";
        string stylePath = Application.streamingAssetsPath + "/cat.jpg";
        string outPath = Application.persistentDataPath + "/outimage.jpg";

        Debug.Log(string.Format("Transfer Param: {0} : {1} :{2}", contentPath, stylePath, outPath));




        yield return StartCoroutine(LoadContentTexture(contentPath));
        yield return StartCoroutine(LoadStyleTexture(stylePath));


#if UNITY_ANDROID //&& !UNITY_EDITOR
        //Unity侧调用Android侧代码
        using (AndroidJavaClass jc = new AndroidJavaClass("com.unity3d.player.UnityPlayer"))
        {
            using (AndroidJavaObject jcurrentActivity = jc.GetStatic<AndroidJavaObject>("currentActivity"))
            {
                AndroidJavaClass jinterface = new AndroidJavaClass("com.bh.utflibrary.UtfLibraryInterface");

                jinterface.CallStatic("Init", jcurrentActivity);


                //AndroidJavaClass jenvironment = new AndroidJavaClass("android.os.Environment");
                //string path = jinterface.CallStatic<AndroidJavaObject>("getExternalStorageDirectory").Call<string>("toString");

                Debug.Log("Start Convert Array");



                AndroidJNI.AttachCurrentThread();

                jvalue[] jparam = new jvalue[6];

                jvalue contentJArr, styleJArr;
                jparam[0].i = contentWidth;
                jparam[1].i = contentHeight;
                jparam[2] = contentJArr = ConvertFloatArrayToJNI(contentData);

                jparam[3].i = styleWidth;
                jparam[4].i = styleHeight;
                jparam[5] = styleJArr = ConvertFloatArrayToJNI(styleData);


                Debug.Log("Start Transfer");

                IntPtr jinterfaceAddr = AndroidJNI.FindClass("com/bh/utflibrary/UtfLibraryInterface");
                IntPtr methodAddr = AndroidJNI.GetStaticMethodID(jinterfaceAddr, "Transfer", "com/bh/utflibrary/UtfLibraryInterface");

                Debug.Log("Call Transfer");
                IntPtr addr = AndroidJNI.CallStaticObjectMethod(jinterfaceAddr, methodAddr, jparam);

                Debug.Log("End Call Transfer");
                outimageData = AndroidJNI.FromIntArray(addr);
                Debug.Log("End Transfer:" + outimageData.Length);

                //int[] arrObj = jinterface.CallStatic<int[]>("Transfer", contentWidth, contentHeight, contentData, styleWidth, styleHeight, styleData);

                //Debug.Log("End Transfer:" + arrObj.Length);
                //if (arrObj.GetRawObject().ToInt32() != 0)
                //{
                //    // String[] returned with some data!
                //    outimageData = AndroidJNIHelper.ConvertFromJNIArray<int[]>
                //                          (arrObj.GetRawObject());

                //    Debug.Log("outimageData Has Values");

                //}
                //else
                //{

                //    Debug.Log("outimageData zero");
                //}

            }
        }
#endif
        Debug.Log("Start Gan");
        if (outimageData != null)
        {
            Debug.Log("Gan Succ");
            Color[] outColor = new Color[outimageData.Length / 3];

            for (int i = 0; i < outimageData.Length / 3; i++)
            {
                Color c = new Color();
                c.r = outimageData[i * 3];
                c.g = outimageData[i * 3 + 1];
                c.b = outimageData[i * 3 + 2];

                outColor[i] = c;
            }


            Texture2D outTex = new Texture2D(contentWidth, contentHeight);
            outTex.SetPixels(outColor);
            Debug.Log("Gan Length:" + outimageData.Length);

            byte[] outData = outTex.EncodeToJPG(100);
            File.WriteAllBytes(outPath, outData);

            OutImage.texture = outTex;
        }
        else
        {

            Debug.Log("Gan Failed");
        }

    }
}
