package com.example.administrator.ybdriver.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.example.administrator.ybdriver.R;
import com.example.administrator.ybdriver.app.AppContext;
import com.example.administrator.ybdriver.canstants.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;


/**
 * 应用程序异常类：用于捕获异常和提示错误信息
 * 
 * @author lixiang
 * @version 1.2
 * @created 2014-10-11
 */
@SuppressWarnings("serial")
public class ExceptionHandler extends Exception implements UncaughtExceptionHandler {

	private final static boolean Debug = true;// 是否保存错误日志
	private static final String TAG = "ExceptionHandler";

	/** 定义异常类型 */
	public final static byte TYPE_NETWORK = 0x01;
	public final static byte TYPE_SOCKET = 0x02;
	public final static byte TYPE_HTTP_CODE = 0x03;
	public final static byte TYPE_HTTP_ERROR = 0x04;
	public final static byte TYPE_XML = 0x05;
	public final static byte TYPE_IO = 0x06;
	public final static byte TYPE_RUN = 0x07;
	private SimpleDateFormat sdf;

	private byte type;
	private int code;

	/** 系统默认的UncaughtException处理类 */
	private UncaughtExceptionHandler mDefaultHandler;

	private ExceptionHandler() {
		this.sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
	}

	private ExceptionHandler(byte type, int code, Exception excp) {
		super(excp);
		this.type = type;
		this.code = code;
		if (Debug) {
			this.saveErrorLog(excp);
		}
	}

	public int getCode() {
		return this.code;
	}

	public int getType() {
		return this.type;
	}

	/**
	 * 提示友好的错误信息
	 * 
	 * @param ctx
	 */
	public void makeToast(Context ctx) {
		switch (this.getType()) {
		case TYPE_HTTP_CODE:
			String err = ctx.getString(R.string.http_status_code_error, this.getCode());
			Toast.makeText(ctx, err, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_HTTP_ERROR:
			Toast.makeText(ctx, R.string.http_exception_error, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_SOCKET:
			Toast.makeText(ctx, R.string.socket_exception_error, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_NETWORK:
			// Toast.makeText(ctx, R.string.network_not_connected,
			// Toast.LENGTH_SHORT).show();
			break;
		case TYPE_XML:
			Toast.makeText(ctx, R.string.xml_parser_failed, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_IO:
			Toast.makeText(ctx, R.string.io_exception_error, Toast.LENGTH_SHORT).show();
			break;
		case TYPE_RUN:
			Toast.makeText(ctx, R.string.app_run_code_error, Toast.LENGTH_SHORT).show();
			break;
		default: {
			ctx.stopService(new Intent(Constants.VERSINO_UPDATE_ACTION));
		}
		}
	}

	private static final long maxFileSize = 1000 * 1000 * 1;// 1M

	/**
	 * 保存异常日志
	 * 
	 * @param excp
	 */
	public void saveErrorLog(Throwable excp) {
		String errorlog = "doovLog.txt";
		String logFilePath = "";
		FileOutputStream out = null;

		try {
			// 判断是否挂载了SD卡
			String storageState = Environment.getExternalStorageState();
			if (storageState.equals(Environment.MEDIA_MOUNTED)) {
				String savePath = Constants.LOG_SAVE_PATH + "Log/";
				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdirs();
				}
				logFilePath = savePath + errorlog;
			}

			if (TextUtils.isEmpty(logFilePath)) {
				Log.e(TAG, "没有挂载SD卡，无法写文件");
				return;
			}

			File logFile = new File(logFilePath);
			if (!logFile.exists()) {
				logFile.createNewFile();
			} else if (logFile.length() > maxFileSize) {
				logFile.delete();
				logFile.createNewFile();
			}
			StringBuffer sb = new StringBuffer();
			sb.append((sdf.format(System.currentTimeMillis())) + "\n");
			sb.append(excp.getMessage() + "\n");
			for (StackTraceElement ste : excp.getStackTrace()) {
				sb.append(ste.getClassName() + ":" + ste.getMethodName() + "()LINE:" + ste.getLineNumber() + "\n");
			}
			String errorMsg = sb.toString();
			StringReader mReader = new StringReader(errorMsg);
			int mByte = 0;
			out = new FileOutputStream(logFile);
			while ((mByte = mReader.read()) != 1) {
				out.write(mByte);
			}
			out.flush();
			out.close();
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 获取APP异常崩溃处理对象
	 *
	 * @return
	 */
	public static ExceptionHandler getAppExceptionHandler() {
		return new ExceptionHandler();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// handleException(ex);
		AppContext.getInstance().stopService(new Intent(Constants.VERSINO_UPDATE_ACTION));
		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		}
		ex.printStackTrace();
	}

	/**
	 * 自定义异常处理:收集错误信息&发送错误报告
	 * 
	 * @param ex
	 * @return true:处理了该异常信息;否则返回false
	 */
	private boolean handleException(final Throwable ex) {
		if (ex == null) {
			return false;
		}
		// 显示异常信息&发送报告
		new Thread() {
			public void run() {
				Looper.prepare();
				// UIHelper.sendAppCrashReport(context, crashReport);
				if (Debug) {
					saveErrorLog(ex);
				}
				Looper.loop();
			}

		}.start();
		return true;
	}

}
