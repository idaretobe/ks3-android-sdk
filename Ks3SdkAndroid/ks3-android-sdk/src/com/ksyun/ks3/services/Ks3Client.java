package com.ksyun.ks3.services;

import java.io.File;
import java.util.List;

import org.apache.http.Header;

import android.content.Context;

import com.ksyun.ks3.exception.Ks3Error;
import com.ksyun.ks3.model.ObjectMetadata;
import com.ksyun.ks3.model.PartETag;
import com.ksyun.ks3.model.acl.AccessControlList;
import com.ksyun.ks3.model.acl.Authorization;
import com.ksyun.ks3.model.acl.CannedAccessControlList;
import com.ksyun.ks3.model.result.CompleteMultipartUploadResult;
import com.ksyun.ks3.model.result.CopyResult;
import com.ksyun.ks3.model.result.HeadObjectResult;
import com.ksyun.ks3.model.result.InitiateMultipartUploadResult;
import com.ksyun.ks3.model.result.ListPartsResult;
import com.ksyun.ks3.services.handler.AbortMultipartUploadResponseHandler;
import com.ksyun.ks3.services.handler.CompleteMultipartUploadResponseHandler;
import com.ksyun.ks3.services.handler.CopyObjectResponseHandler;
import com.ksyun.ks3.services.handler.GetObjectResponseHandler;
import com.ksyun.ks3.services.handler.HeadObjectResponseHandler;
import com.ksyun.ks3.services.handler.InitiateMultipartUploadResponceHandler;
import com.ksyun.ks3.services.handler.ListPartsResponseHandler;
import com.ksyun.ks3.services.handler.PutObjectResponseHandler;
import com.ksyun.ks3.services.handler.UploadPartResponceHandler;
import com.ksyun.ks3.services.request.AbortMultipartUploadRequest;
import com.ksyun.ks3.services.request.CompleteMultipartUploadRequest;
import com.ksyun.ks3.services.request.CopyObjectRequest;
import com.ksyun.ks3.services.request.GetObjectRequest;
import com.ksyun.ks3.services.request.HeadObjectRequest;
import com.ksyun.ks3.services.request.InitiateMultipartUploadRequest;
import com.ksyun.ks3.services.request.Ks3HttpRequest;
import com.ksyun.ks3.services.request.ListPartsRequest;
import com.ksyun.ks3.services.request.PutObjectRequest;
import com.ksyun.ks3.services.request.UploadPartRequest;
import com.ksyun.ks3.util.Constants;
import com.loopj.android.http.AsyncHttpResponseHandler;

public class Ks3Client implements Ks3 {
	private Ks3ClientConfiguration clientConfiguration;
	private String endpoint;
	private Authorization auth;
	private Ks3HttpExector client = new Ks3HttpExector();
	private Context context = null;
	public AuthListener authListener = null;

	public Ks3Client(String accesskeyid, String accesskeysecret, Context context) {
		this(accesskeyid, accesskeysecret, Ks3ClientConfiguration
				.getDefaultConfiguration(), context);
	}

	public Ks3Client(String accesskeyid, String accesskeysecret,
			Ks3ClientConfiguration clientConfiguration, Context context) {
		this.auth = new Authorization(accesskeyid, accesskeysecret);
		this.clientConfiguration = clientConfiguration;
		this.context = context;
		init();
	}

	public Ks3Client(Authorization auth, Context context) {
		this(auth, Ks3ClientConfiguration.getDefaultConfiguration(), context);
	}

	public Ks3Client(Authorization auth,
			Ks3ClientConfiguration clientConfiguration, Context context) {
		this.auth = auth;
		this.clientConfiguration = clientConfiguration;
		this.context = context;
		init();
	}

	public Ks3Client(AuthListener listener, Context context) {
		this(listener, Ks3ClientConfiguration.getDefaultConfiguration(),
				context);
	}

	public Ks3Client(AuthListener listener,
			Ks3ClientConfiguration clientConfiguration, Context context) {
		this.authListener = listener;
		this.clientConfiguration = clientConfiguration;
		this.context = context;
		init();
	}

	private void init() {
		setEndpoint(Constants.ClientConfig_END_POINT);
	}

	public void setConfiguration(Ks3ClientConfiguration clientConfiguration) {
		this.clientConfiguration = clientConfiguration;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public AuthListener getAuthListener() {
		return authListener;
	}

	public void setAuthListener(AuthListener authListener) {
		this.authListener = authListener;
	}

	/* Service */

	@Override
	public Ks3HttpRequest getObject(Context context, String bucketname,
			String key, GetObjectResponseHandler handler) {
		this.context = context;
		return this.getObject(new GetObjectRequest(bucketname, key), handler);
	}

	@Override
	public Ks3HttpRequest getObject(GetObjectRequest request,
			GetObjectResponseHandler handler) {
		return this.getObject(request, handler, true);
	}

	private Ks3HttpRequest getObject(GetObjectRequest request,
			GetObjectResponseHandler handler, boolean isUseAsyncMode) {
		return this.invoke(auth, request, handler, isUseAsyncMode);
	}

	@Override
	public Ks3HttpRequest putObject(String bucketname, String objectkey,
			File file, PutObjectResponseHandler handler) {
		return this.putObject(
				new PutObjectRequest(bucketname, objectkey, file), handler);
	}

	@Override
	public Ks3HttpRequest putObject(String bucketname, String objectkey,
			File file, ObjectMetadata objectmeta,
			PutObjectResponseHandler handler) {
		return this.putObject(new PutObjectRequest(bucketname, objectkey, file,
				objectmeta), handler);
	}

	@Override
	public Ks3HttpRequest putObject(PutObjectRequest request,
			PutObjectResponseHandler handler) {
		return this.putObject(request, handler, true);
	}

	private Ks3HttpRequest putObject(PutObjectRequest request,
			PutObjectResponseHandler handler, boolean isUseAsyncMode) {
		return this.invoke(auth, request, handler, isUseAsyncMode);
	}

	@Override
	public void headObject(String bucketname, String objectkey,
			HeadObjectResponseHandler resultHandler) {
		this.headObject(new HeadObjectRequest(bucketname, objectkey),
				resultHandler);
	}

	@Override
	public void headObject(HeadObjectRequest request,
			HeadObjectResponseHandler resultHandler) {
		this.headObject(request, resultHandler, true);
	}

	private void headObject(HeadObjectRequest request,
			HeadObjectResponseHandler resultHandler, boolean isUseAsyncMode) {
		this.invoke(auth, request, resultHandler, isUseAsyncMode);
	}

	@Override
	public void copyObject(String destinationBucket, String destinationObject,
			String sourceBucket, String sourceKey,
			CopyObjectResponseHandler handler) {
		CopyObjectRequest request = new CopyObjectRequest(destinationBucket,
				destinationObject, sourceBucket, sourceKey);
		this.copyObject(request, handler);

	}

	@Override
	public void copyObject(String destinationBucket, String destinationObject,
			String sourceBucket, String sourceKey,
			CannedAccessControlList cannedAcl, CopyObjectResponseHandler handler) {
		CopyObjectRequest request = new CopyObjectRequest(destinationBucket,
				destinationObject, sourceBucket, sourceKey, cannedAcl);
		this.copyObject(request, handler);

	}

	@Override
	public void copyObject(String destinationBucket, String destinationObject,
			String sourceBucket, String sourceKey,
			AccessControlList accessControlList,
			CopyObjectResponseHandler handler) {

		CopyObjectRequest request = new CopyObjectRequest(destinationBucket,
				destinationObject, sourceBucket, sourceKey, accessControlList);
		this.copyObject(request, handler);

	}

	@Override
	public void copyObject(CopyObjectRequest request,
			CopyObjectResponseHandler handler) {
		this.copyObject(request, handler, true);
	}

	private void copyObject(CopyObjectRequest request,
			CopyObjectResponseHandler handler, boolean isUseAsyncMode) {
		this.invoke(auth, request, handler, isUseAsyncMode);
	}

	/* MultiUpload */
	@Override
	public void initiateMultipartUpload(String bucketname, String objectkey,
			InitiateMultipartUploadResponceHandler resultHandler) {
		this.initiateMultipartUpload(new InitiateMultipartUploadRequest(
				bucketname, objectkey), resultHandler);
	}

	@Override
	public void initiateMultipartUpload(InitiateMultipartUploadRequest request,
			InitiateMultipartUploadResponceHandler resultHandler) {
		this.initiateMultipartUpload(request, resultHandler, true);
	}

	private void initiateMultipartUpload(
			InitiateMultipartUploadRequest request,
			InitiateMultipartUploadResponceHandler resultHandler,
			boolean isUseAsyncMode) {
		this.invoke(auth, request, resultHandler, isUseAsyncMode);
	}

	@Override
	public void uploadPart(String bucketName, String key, String uploadId,
			File file, long offset, int partNumber, long partSize,
			UploadPartResponceHandler resultHandler) {
		this.uploadPart(new UploadPartRequest(bucketName, key, uploadId, file,
				offset, partNumber, partSize), resultHandler);
	}

	@Override
	public void uploadPart(UploadPartRequest request,
			UploadPartResponceHandler resultHandler) {
		this.uploadPart(request, resultHandler, true);
	}

	private void uploadPart(UploadPartRequest request,
			UploadPartResponceHandler resultHandler, boolean isUseAsyncMode) {
		this.invoke(auth, request, resultHandler, isUseAsyncMode);
	}

	@Override
	public void completeMultipartUpload(String bucketname, String objectkey,
			String uploadId, List<PartETag> partETags,
			CompleteMultipartUploadResponseHandler handler) {
		this.completeMultipartUpload(new CompleteMultipartUploadRequest(
				bucketname, objectkey, uploadId, partETags), handler);
	}

	@Override
	public void completeMultipartUpload(ListPartsResult result,
			CompleteMultipartUploadResponseHandler handler) {
		this.completeMultipartUpload(
				new CompleteMultipartUploadRequest(result), handler);
	}

	@Override
	public void completeMultipartUpload(CompleteMultipartUploadRequest request,
			CompleteMultipartUploadResponseHandler handler) {
		this.completeMultipartUpload(request, handler, true);
	}

	private void completeMultipartUpload(
			CompleteMultipartUploadRequest request,
			CompleteMultipartUploadResponseHandler handler,
			boolean isUseAsyncMode) {
		this.invoke(auth, request, handler, isUseAsyncMode);
	}

	@Override
	public void abortMultipartUpload(String bucketname, String objectkey,
			String uploadId, AbortMultipartUploadResponseHandler handler) {
		this.abortMultipartUpload(new AbortMultipartUploadRequest(bucketname,
				objectkey, uploadId), handler);
	}

	@Override
	public void abortMultipartUpload(AbortMultipartUploadRequest request,
			AbortMultipartUploadResponseHandler handler) {
		this.abortMultipartUpload(request, handler, true);
	}

	private void abortMultipartUpload(AbortMultipartUploadRequest request,
			AbortMultipartUploadResponseHandler handler, boolean isUseAsyncMode) {
		this.invoke(auth, request, handler, isUseAsyncMode);
	}

	@Override
	public void listParts(String bucketname, String objectkey, String uploadId,
			ListPartsResponseHandler handler) {
		this.listParts(new ListPartsRequest(bucketname, objectkey, uploadId),
				handler);
	}

	@Override
	public void listParts(String bucketname, String objectkey, String uploadId,
			int maxParts, ListPartsResponseHandler handler) {
		this.listParts(new ListPartsRequest(bucketname, objectkey, uploadId,
				maxParts), handler);
	}

	@Override
	public void listParts(String bucketname, String objectkey, String uploadId,
			int maxParts, int partNumberMarker, ListPartsResponseHandler handler) {
		this.listParts(new ListPartsRequest(bucketname, objectkey, uploadId,
				maxParts, partNumberMarker), handler);
	}

	@Override
	public void listParts(ListPartsRequest request,
			ListPartsResponseHandler handler) {
		this.listParts(request, handler, true);
	}

	private void listParts(ListPartsRequest request,
			ListPartsResponseHandler handler, boolean isUseAsyncMode) {
		this.invoke(auth, request, handler, isUseAsyncMode);
	}

	/* Invoke asnyc http client */
	private Ks3HttpRequest invoke(Authorization auth, Ks3HttpRequest request,
			AsyncHttpResponseHandler resultHandler, boolean isUseAsyncMode) {
		client.invoke(auth, request, resultHandler, clientConfiguration,
				context, endpoint, authListener, isUseAsyncMode);
		return request;
	}

	@Override
	public void pause(Context context) {
		client.pause(context);
	}

	@Override
	public void cancel(Context context) {
		client.cancel(context);
	}

	@Override
	public Context getContext() {
		return this.context;
	}

	@Override
	public HeadObjectResult syncHeadObject(String bucketName, String objectKey)
			throws Throwable {
		HeadObjectRequest request = new HeadObjectRequest(bucketName, objectKey);
		return this.syncHeadObject(request);
	}

	@Override
	public HeadObjectResult syncHeadObject(HeadObjectRequest request)
			throws Throwable {

		final HeadObjectResult result = new HeadObjectResult();
		final Throwable error = new Throwable();
		this.headObject(request, new HeadObjectResponseHandler() {

			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders,
					HeadObjectResult headObjectResult) {
				result.setETag(headObjectResult.getETag());
				result.setLastmodified(headObjectResult.getLastmodified());
				result.setObjectMetadata(headObjectResult.getObjectMetadata());

			}

			@Override
			public void onFailure(int statesCode, Ks3Error ks3Error,
					Header[] responceHeaders, String response,
					Throwable paramThrowable) {
				error.initCause(paramThrowable);
				
			}
		}, false);
		if (error.getCause() != null) {
			throw error;
		}
		return result;
	}

	@Override
	public CopyResult syncCopyObject(String destinationBucket,
			String destinationObject, String sourceBucket, String sourceKey)
			throws Throwable {
		CopyObjectRequest request = new CopyObjectRequest(destinationBucket,
				destinationObject, sourceBucket, sourceKey);
		return this.syncCopyObject(request);
	}

	@Override
	public CopyResult syncCopyObject(String destinationBucket,
			String destinationObject, String sourceBucket, String sourceKey,
			CannedAccessControlList accessControlList) throws Throwable {
		CopyObjectRequest request = new CopyObjectRequest(destinationBucket,
				destinationObject, sourceBucket, sourceKey, accessControlList);
		return this.syncCopyObject(request);
	}

	@Override
	public CopyResult syncCopyObject(String destinationBucket,
			String destinationObject, String sourceBucket, String sourceKey,
			AccessControlList accessControlList) throws Throwable {
		CopyObjectRequest request = new CopyObjectRequest(destinationBucket,
				destinationObject, sourceBucket, sourceKey, accessControlList);
		return this.syncCopyObject(request);
	}

	@Override
	public CopyResult syncCopyObject(CopyObjectRequest request)
			throws Throwable {

		final CopyResult copyResult = new CopyResult();
		final Throwable error = new Throwable();
		this.copyObject(request, new CopyObjectResponseHandler() {

			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders,
					CopyResult result) {
				copyResult.setETag(result.getETag());
				copyResult.setLastModified(result.getLastModified());

			}

			@Override
			public void onFailure(int statesCode, Ks3Error ks3Error,
					Header[] responceHeaders, String response,
					Throwable paramThrowable) {
				error.initCause(paramThrowable);
			}
		}, false);
		if (error.getCause() != null) {
			throw error;
		}
		return copyResult;
	}

	@Override
	public InitiateMultipartUploadResult syncInitiateMultipartUpload(
			String bucketName, String objectKey) throws Throwable {
		InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(
				bucketName, objectKey);
		return this.syncInitiateMultipartUpload(request);
	}

	@Override
	public InitiateMultipartUploadResult syncInitiateMultipartUpload(
			InitiateMultipartUploadRequest request) throws Throwable {

		final InitiateMultipartUploadResult initResult = new InitiateMultipartUploadResult();
		final Throwable error = new Throwable();
		this.initiateMultipartUpload(request,
				new InitiateMultipartUploadResponceHandler() {

					@Override
					public void onSuccess(int statesCode,
							Header[] responceHeaders,
							InitiateMultipartUploadResult result) {
						initResult.setBucket(result.getBucket());
						initResult.setKey(result.getKey());
						initResult.setUploadId(result.getUploadId());

					}

					@Override
					public void onFailure(int statesCode, Ks3Error ks3Error,
							Header[] responceHeaders, String response,
							Throwable paramThrowable) {
						error.initCause(paramThrowable);
					}
				}, false);
		if (error.getCause() != null) {
			throw error;
		}
		return initResult;
	}

	@Override
	public CompleteMultipartUploadResult syncCompleteMultipartUpload(
			String bucketName, String objectKey, String uploadId,
			List<PartETag> partETags) throws Throwable {
		CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest(
				bucketName, objectKey, uploadId, partETags);
		return this.syncCompleteMultipartUpload(request);
	}

	@Override
	public CompleteMultipartUploadResult syncCompleteMultipartUpload(
			ListPartsResult result) throws Throwable {
		CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest(
				result);
		return this.syncCompleteMultipartUpload(request);
	}

	@Override
	public CompleteMultipartUploadResult syncCompleteMultipartUpload(
			CompleteMultipartUploadRequest request) throws Throwable {

		final CompleteMultipartUploadResult completeMultipartUploadResult = new CompleteMultipartUploadResult();
		final Throwable error = new Throwable();
		this.completeMultipartUpload(request,
				new CompleteMultipartUploadResponseHandler() {

					@Override
					public void onSuccess(int statesCode,
							Header[] responceHeaders,
							CompleteMultipartUploadResult result) {
						completeMultipartUploadResult.setBucket(result
								.getBucket());
						completeMultipartUploadResult.setKey(result.getKey());
						completeMultipartUploadResult.seteTag(result.geteTag());
						completeMultipartUploadResult.setLocation(result
								.getLocation());

					}

					@Override
					public void onFailure(int statesCode, Ks3Error ks3Error,
							Header[] responceHeaders, String response,
							Throwable paramThrowable) {
						error.initCause(paramThrowable);
					}
				}, false);
		if (error.getCause() != null) {
			throw error;
		}
		return completeMultipartUploadResult;
	}

	@Override
	public void syncAbortMultipartUpload(AbortMultipartUploadRequest request)
			throws Throwable {

		final Throwable error = new Throwable();
		this.abortMultipartUpload(request,
				new AbortMultipartUploadResponseHandler() {

					@Override
					public void onSuccess(int statesCode,
							Header[] responceHeaders) {

					}

					@Override
					public void onFailure(int statesCode, Ks3Error ks3Error,
							Header[] responceHeaders, String response,
							Throwable paramThrowable) {
						error.initCause(paramThrowable);
						
					}
				}, false);
		if (error.getCause() != null) {
			throw error;
		}
	}

	@Override
	public void syncAbortMultipartUpload(String bucketname, String objectKey,
			String uploadId) throws Throwable {
		AbortMultipartUploadRequest request = new AbortMultipartUploadRequest(
				bucketname, objectKey, uploadId);
		this.syncAbortMultipartUpload(request);
	}

	@Override
	public ListPartsResult syncListParts(String bucketName, String objectKey,
			String uploadId) throws Throwable {
		ListPartsRequest request = new ListPartsRequest(bucketName, objectKey,
				uploadId);
		return this.syncListParts(request);
	}

	@Override
	public ListPartsResult syncListParts(String bucketName, String objectKey,
			String uploadId, int maxParts) throws Throwable {
		ListPartsRequest request = new ListPartsRequest(bucketName, objectKey,
				uploadId, maxParts);
		return this.syncListParts(request);
	}

	@Override
	public ListPartsResult syncListParts(String bucketName, String objectKey,
			String uploadId, int maxParts, int partNumberMarker)
			throws Throwable {
		ListPartsRequest request = new ListPartsRequest(bucketName, objectKey,
				uploadId, maxParts, partNumberMarker);
		return this.syncListParts(request);
	}

	@Override
	public ListPartsResult syncListParts(ListPartsRequest request)
			throws Throwable {
		final ListPartsResult listPartsResult = new ListPartsResult();
		final Throwable error = new Throwable();
		this.listParts(request, new ListPartsResponseHandler() {

			@Override
			public void onSuccess(int statesCode, Header[] responceHeaders,
					ListPartsResult result) {
				listPartsResult.setBucketname(result.getBucketname());
				listPartsResult.setEncodingType(result.getEncodingType());
				listPartsResult.setInitiator(result.getInitiator());
				listPartsResult.setKey(result.getKey());
				listPartsResult.setMaxParts(result.getMaxParts());
				listPartsResult.setNextPartNumberMarker(result
						.getNextPartNumberMarker());
				listPartsResult.setOwner(result.getOwner());
				listPartsResult.setPartNumberMarker(result
						.getPartNumberMarker());
				listPartsResult.setParts(result.getParts());
				listPartsResult.setUploadId(result.getUploadId());
			}

			@Override
			public void onFailure(int statesCode, Ks3Error ks3Error,
					Header[] responceHeaders, String response,
					Throwable paramThrowable) {
				error.initCause(paramThrowable);
				
			}
		}, false);
		if (error.getCause() != null) {
			throw error;
		}
		return listPartsResult;
	}
}
