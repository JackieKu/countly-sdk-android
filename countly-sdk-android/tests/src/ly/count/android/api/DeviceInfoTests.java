/*
Copyright (c) 2012, 2013, 2014 Countly

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package ly.count.android.api;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.telephony.TelephonyManager;
import android.test.AndroidTestCase;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

import static org.mockito.Mockito.*;

public class DeviceInfoTests extends AndroidTestCase {

    public void testUDID() {
        final String deviceID = "1234";
        DeviceInfo.setUDID(deviceID);
        assertEquals(deviceID, DeviceInfo.getUDID());
    }

    public void testGetOS() {
        assertEquals("Android", DeviceInfo.getOS());
    }

    public void testGetOSVersion() {
        assertEquals(android.os.Build.VERSION.RELEASE, DeviceInfo.getOSVersion());
    }

    public void testGetDevice() {
        assertEquals(android.os.Build.MODEL, DeviceInfo.getDevice());
    }

    public void testGetResolution() {
        final DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        final String expected = metrics.widthPixels + "x" + metrics.heightPixels;
        assertEquals(expected, DeviceInfo.getResolution(getContext()));
    }

    private Context mockContextForTestingDensity(final int density) {
        final DisplayMetrics metrics = new DisplayMetrics();
        metrics.densityDpi = density;
        final Resources mockResources = mock(Resources.class);
        when(mockResources.getDisplayMetrics()).thenReturn(metrics);
        final Context mockContext = mock(Context.class);
        when(mockContext.getResources()).thenReturn(mockResources);
        return mockContext;
    }

    public void testGetDensity() {
        Context mockContext = mockContextForTestingDensity(DisplayMetrics.DENSITY_LOW);
        assertEquals("LDPI", DeviceInfo.getDensity(mockContext));
        mockContext = mockContextForTestingDensity(DisplayMetrics.DENSITY_MEDIUM);
        assertEquals("MDPI", DeviceInfo.getDensity(mockContext));
        mockContext = mockContextForTestingDensity(DisplayMetrics.DENSITY_TV);
        assertEquals("TVDPI", DeviceInfo.getDensity(mockContext));
        mockContext = mockContextForTestingDensity(DisplayMetrics.DENSITY_HIGH);
        assertEquals("HDPI", DeviceInfo.getDensity(mockContext));
        mockContext = mockContextForTestingDensity(DisplayMetrics.DENSITY_XHIGH);
        assertEquals("XHDPI", DeviceInfo.getDensity(mockContext));
        mockContext = mockContextForTestingDensity(DisplayMetrics.DENSITY_XXHIGH);
        assertEquals("XXHDPI", DeviceInfo.getDensity(mockContext));
        mockContext = mockContextForTestingDensity(DisplayMetrics.DENSITY_XXXHIGH);
        assertEquals("XXXHDPI", DeviceInfo.getDensity(mockContext));
        mockContext = mockContextForTestingDensity(DisplayMetrics.DENSITY_400);
        assertEquals("XMHDPI", DeviceInfo.getDensity(mockContext));
        mockContext = mockContextForTestingDensity(0);
        assertEquals("", DeviceInfo.getDensity(mockContext));
    }

    public void testGetCarrier_nullTelephonyManager() {
        final Context mockContext = mock(Context.class);
        when(mockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(null);
        assertEquals("", DeviceInfo.getCarrier(mockContext));
    }

    public void testGetCarrier_nullNetOperator() {
        final TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
        when(mockTelephonyManager.getNetworkOperatorName()).thenReturn(null);
        final Context mockContext = mock(Context.class);
        when(mockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
        assertEquals("", DeviceInfo.getCarrier(mockContext));
    }

    public void testGetCarrier_emptyNetOperator() {
        final TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
        when(mockTelephonyManager.getNetworkOperatorName()).thenReturn("");
        final Context mockContext = mock(Context.class);
        when(mockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
        assertEquals("", DeviceInfo.getCarrier(mockContext));
    }

    public void testGetCarrier() {
        final TelephonyManager mockTelephonyManager = mock(TelephonyManager.class);
        when(mockTelephonyManager.getNetworkOperatorName()).thenReturn("Verizon");
        final Context mockContext = mock(Context.class);
        when(mockContext.getSystemService(Context.TELEPHONY_SERVICE)).thenReturn(mockTelephonyManager);
        assertEquals("Verizon", DeviceInfo.getCarrier(mockContext));
    }

    public void testGetLocale() {
        final Locale defaultLocale = Locale.getDefault();
        try {
            Locale.setDefault(new Locale("ab", "CD"));
            assertEquals("ab_CD", DeviceInfo.getLocale());
        } finally {
            Locale.setDefault(defaultLocale);
        }
    }

    public void testGetAppVersion() throws PackageManager.NameNotFoundException {
        final PackageInfo pkgInfo = new PackageInfo();
        pkgInfo.versionName = "42.0";
        final String fakePkgName = "i.like.chicken";
        final PackageManager mockPkgMgr = mock(PackageManager.class);
        when(mockPkgMgr.getPackageInfo(fakePkgName, 0)).thenReturn(pkgInfo);
        final Context mockContext = mock(Context.class);
        when(mockContext.getPackageName()).thenReturn(fakePkgName);
        when(mockContext.getPackageManager()).thenReturn(mockPkgMgr);
        assertEquals("42.0", DeviceInfo.getAppVersion(mockContext));
    }

    public void testGetAppVersion_pkgManagerThrows() throws PackageManager.NameNotFoundException {
        final String fakePkgName = "i.like.chicken";
        final PackageManager mockPkgMgr = mock(PackageManager.class);
        when(mockPkgMgr.getPackageInfo(fakePkgName, 0)).thenThrow(new PackageManager.NameNotFoundException());
        final Context mockContext = mock(Context.class);
        when(mockContext.getPackageName()).thenReturn(fakePkgName);
        when(mockContext.getPackageManager()).thenReturn(mockPkgMgr);
        assertEquals("1.0", DeviceInfo.getAppVersion(mockContext));
    }

    public void testGetMetrics() throws UnsupportedEncodingException, JSONException {
        final JSONObject json = new JSONObject();
        json.put("_device", DeviceInfo.getDevice());
        json.put("_os", DeviceInfo.getOS());
        json.put("_os_version", DeviceInfo.getOSVersion());
        json.put("_carrier", DeviceInfo.getCarrier(getContext()));
        json.put("_resolution", DeviceInfo.getResolution(getContext()));
        json.put("_density", DeviceInfo.getDensity(getContext()));
        json.put("_locale", DeviceInfo.getLocale());
        json.put("_app_version", DeviceInfo.getAppVersion(getContext()));
        final String expected = URLEncoder.encode(json.toString(), "UTF-8");
        assertNotNull(expected);
        assertEquals(expected, DeviceInfo.getMetrics(getContext()));
    }
}
