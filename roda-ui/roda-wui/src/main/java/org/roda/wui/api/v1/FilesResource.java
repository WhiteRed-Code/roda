/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE file at the root of the source
 * tree and available online at
 *
 * https://github.com/keeps/roda
 */
package org.roda.wui.api.v1;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.xml.transform.TransformerException;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.roda.core.common.StreamResponse;
import org.roda.core.common.UserUtility;
import org.roda.core.data.common.RodaConstants;
import org.roda.core.data.exceptions.RODAException;
import org.roda.core.data.v2.ip.Files;
import org.roda.core.data.v2.ip.IndexedFile;
import org.roda.core.data.v2.ip.metadata.PreservationMetadata;
import org.roda.core.data.v2.user.RodaUser;
import org.roda.wui.api.controllers.Browser;
import org.roda.wui.api.v1.utils.ApiResponseMessage;
import org.roda.wui.api.v1.utils.ApiUtils;
import org.roda.wui.api.v1.utils.EntityResponse;
import org.roda.wui.api.v1.utils.ObjectResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Path(FilesResource.ENDPOINT)
@Api(value = FilesResource.SWAGGER_ENDPOINT)
public class FilesResource {
  public static final String ENDPOINT = "/v1/files";
  public static final String SWAGGER_ENDPOINT = "v1 files";

  @Context
  private HttpServletRequest request;

  @GET
  @ApiOperation(value = "List Files", notes = "Gets a list of files.", response = File.class, responseContainer = "List")
  @ApiResponses(value = {
    @ApiResponse(code = 200, message = "Successful response", response = File.class, responseContainer = "List")})

  public Response listFiles(
    @ApiParam(value = "Index of the first element to return", defaultValue = "0") @QueryParam(RodaConstants.API_QUERY_KEY_START) String start,
    @ApiParam(value = "Maximum number of elements to return", defaultValue = RodaConstants.DEFAULT_PAGINATION_STRING_VALUE) @QueryParam(RodaConstants.API_QUERY_KEY_LIMIT) String limit,
    @ApiParam(value = "Choose format in which to get the file", allowableValues = "json, xml", defaultValue = RodaConstants.API_QUERY_VALUE_ACCEPT_FORMAT_JSON) @QueryParam(RodaConstants.API_QUERY_KEY_ACCEPT_FORMAT) String acceptFormat)
    throws RODAException {
    String mediaType = ApiUtils.getMediaType(acceptFormat, request);

    // get user
    RodaUser user = UserUtility.getApiUser(request);

    // delegate action to controller
    Files files = (Files) Browser.retrieveObjects(user, IndexedFile.class, start, limit, acceptFormat);
    return Response.ok(files, mediaType).build();
  }

  @GET
  @Path("/{" + RodaConstants.API_PATH_PARAM_FILE_UUID + "}")
  @Produces({"application/json", "application/xml", "application/octetstream"})
  @ApiOperation(value = "Get file", notes = "Get file", response = File.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = File.class),
    @ApiResponse(code = 404, message = "Not found", response = File.class)})

  public Response retrieve(
    @ApiParam(value = "The ID of the existing file", required = true) @PathParam(RodaConstants.API_PATH_PARAM_FILE_UUID) String fileUUID,
    @ApiParam(value = "Choose format in which to get the file", allowableValues = "json, xml, bin") @QueryParam(RodaConstants.API_QUERY_KEY_ACCEPT_FORMAT) String acceptFormat)
    throws RODAException {
    String mediaType = ApiUtils.getMediaType(acceptFormat, request);

    // get user
    RodaUser user = UserUtility.getApiUser(request);

    // delegate action to controller
    EntityResponse efile = Browser.retrieveAIPRepresentationFile(user, fileUUID, acceptFormat);

    if (efile instanceof ObjectResponse) {
      ObjectResponse<org.roda.core.data.v2.ip.File> file = (ObjectResponse<org.roda.core.data.v2.ip.File>) efile;
      return Response.ok(file.getObject(), mediaType).build();
    } else {
      return ApiUtils.okResponse((StreamResponse) efile);
    }
  }

  @PUT
  @ApiOperation(value = "Update file", notes = "Update existing file", response = File.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = File.class),
    @ApiResponse(code = 404, message = "Not found", response = File.class)})

  public Response update(org.roda.core.data.v2.ip.File file,
    @ApiParam(value = "Choose format in which to get the file", allowableValues = "json, xml") @QueryParam(RodaConstants.API_QUERY_KEY_ACCEPT_FORMAT) String acceptFormat)
    throws RODAException {
    String mediaType = ApiUtils.getMediaType(acceptFormat, request);

    // get user
    RodaUser user = UserUtility.getApiUser(request);

    // delegate action to controller
    org.roda.core.data.v2.ip.File updatedFile = Browser.updateFile(user, file);
    return Response.ok(updatedFile, mediaType).build();
  }

  @POST
  @ApiOperation(value = "Create file", notes = "Create a new representation file", response = File.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = File.class),
    @ApiResponse(code = 409, message = "Already exists", response = File.class)})

  public Response createRepresentationFile(
    @ApiParam(value = "The ID of the AIP where to create the representation", required = true) @PathParam(RodaConstants.API_PATH_PARAM_AIP_ID) String aipId,
    @ApiParam(value = "The requested ID for the new representation", required = true) @PathParam(RodaConstants.API_PATH_PARAM_REPRESENTATION_ID) String representationId,
    @ApiParam(value = "The requested ID of the new file", required = true) @PathParam(RodaConstants.API_PATH_PARAM_FILE_ID) String fileId,
    @FormDataParam("file") InputStream inputStream, @FormDataParam("file") FormDataContentDisposition fileDetail,
    @ApiParam(value = "Choose format in which to get the file", allowableValues = "json, xml") @QueryParam(RodaConstants.API_QUERY_KEY_ACCEPT_FORMAT) String acceptFormat)
    throws RODAException {
    String mediaType = ApiUtils.getMediaType(acceptFormat, request);

    // get user
    RodaUser user = UserUtility.getApiUser(request);

    // delegate action to controller
    org.roda.core.data.v2.ip.File file;
    try {
      file = Browser.createFile(user, aipId, representationId, new ArrayList<>(), fileId, inputStream);
      return Response.ok(file, mediaType).build();
    } catch (IOException e) {
      return ApiUtils.errorResponse(new TransformerException(e.getMessage()));
    }

  }

  @DELETE
  @Path("/{" + RodaConstants.API_PATH_PARAM_FILE_UUID + "}")
  @ApiOperation(value = "Delete file", notes = "Delete representation file", response = Void.class)
  @ApiResponses(value = {@ApiResponse(code = 204, message = "OK", response = Void.class),
    @ApiResponse(code = 404, message = "Not found", response = Void.class)})

  public Response delete(
    @ApiParam(value = "The ID of the existing file", required = true) @PathParam(RodaConstants.API_PATH_PARAM_FILE_UUID) String fileUUID)
    throws RODAException {
    // get user
    RodaUser user = UserUtility.getApiUser(request);

    // delegate action to controller
    Browser.deleteRepresentationFile(user, fileUUID);
    return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "File was deleted!")).build();
  }

  @GET
  @Path("/{" + RodaConstants.API_PATH_PARAM_FILE_UUID + "}/preservation_metadata/")
  @Produces({"application/json", "application/zip", "text/html"})
  @ApiOperation(value = "Get preservation metadata", notes = "Get preservation metadata (JSON info, ZIP file or HTML conversion).\nOptional query params of **start** and **limit** defined the returned array.", response = PreservationMetadata.class)
  @ApiResponses(value = {@ApiResponse(code = 200, message = "OK", response = PreservationMetadata.class)})

  public Response retrievePreservationMetadataListFromAIP(
    @ApiParam(value = "The ID of the existing AIP", required = true) @PathParam(RodaConstants.API_PATH_PARAM_FILE_UUID) String fileId,
    @ApiParam(value = "Index of the first element to return", defaultValue = "0") @QueryParam(RodaConstants.API_QUERY_KEY_START) String start,
    @ApiParam(value = "Maximum number of elements to return", defaultValue = RodaConstants.DEFAULT_PAGINATION_STRING_VALUE) @QueryParam(RodaConstants.API_QUERY_KEY_LIMIT) String limit,
    @ApiParam(value = "Choose format in which to get the metadata", allowableValues = "json, xml, zip", defaultValue = RodaConstants.API_QUERY_VALUE_ACCEPT_FORMAT_JSON) @QueryParam(RodaConstants.API_QUERY_KEY_ACCEPT_FORMAT) String acceptFormat)
    throws RODAException {
    String mediaType = ApiUtils.getMediaType(acceptFormat, request);

    // get user
    RodaUser user = UserUtility.getApiUser(request);

    // delegate action to controller
    EntityResponse preservationMetadataList = Browser.retrieveAIPRepresentationPreservationMetadataFile(user, fileId,
      acceptFormat);

    if (preservationMetadataList instanceof ObjectResponse) {
      ObjectResponse<PreservationMetadata> aip = (ObjectResponse<PreservationMetadata>) preservationMetadataList;
      return Response.ok(aip.getObject(), mediaType).build();
    } else {
      return ApiUtils.okResponse((StreamResponse) preservationMetadataList);
    }
  }

}
