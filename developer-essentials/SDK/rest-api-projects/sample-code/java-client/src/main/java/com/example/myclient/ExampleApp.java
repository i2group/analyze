package com.example.myclient;

import com.example.generated.api.*;
import com.example.generated.invoker.*;
import com.example.generated.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

public class ExampleApp {
    private static final String SERVER_URL = "https://i2analyze.eia:9443/opal";

    private static final String REQUIRED_API_VERSION = "1.0";
    private static final String CLIENT_NAME = "My Client";
    private static final String CLIENT_VERSION = "0.0.1-demo";

    // You must set this to "SEARCH_V1" to use the experimental APIs.
    private static final String OPT_IN_TO_EXPERIMENTAL_API = ""; // TODO: You must set this

    private final ApiClient apiClient;

    private Map<String, SchemaEntityType> entityTypes;
    private Map<String, SchemaLinkType> linkTypes;
    private Map<String, SchemaPropertyType> propertyTypes;

    public ExampleApp(ApiClient apiClient) {
        this.apiClient = apiClient;
        checkApiVersionIsSupported();
        fetchSchema();
    }

    private void fetchSchema() {
        final Schema schema = executeRequest(() -> new InformationStoreApi(apiClient).getInfoStoreSchema());
        entityTypes = schema.getItemTypes().getEntityTypes().stream().collect(Collectors.toMap(SchemaEntityType::getId, Function.identity()));
        linkTypes = schema.getItemTypes().getLinkTypes().stream().collect(Collectors.toMap(SchemaLinkType::getId, Function.identity()));
        propertyTypes = new HashMap<>();

        Collector<SchemaPropertyType, ?, Map<String, SchemaPropertyType>> collectProperties = Collectors.toMap(SchemaPropertyType::getId, Function.identity());
        propertyTypes.putAll(entityTypes.values().stream().flatMap(type -> Stream.concat(type.getPropertyGroupTypes().stream().map(SchemaPropertyGroupType::getPropertyTypes).flatMap(Collection::stream), type.getPropertyTypes().stream())).collect(collectProperties));
        propertyTypes.putAll(linkTypes.values().stream().flatMap(type -> Stream.concat(type.getPropertyGroupTypes().stream().map(SchemaPropertyGroupType::getPropertyTypes).flatMap(Collection::stream), type.getPropertyTypes().stream())).collect(collectProperties));
    }

    public void checkApiVersionIsSupported() {
        System.out.println("Version negotiation...");
        final VersionApi versionApi = new VersionApi(apiClient);

        final VersionNegotiationRequest request = new VersionNegotiationRequest();
        final String requiredVersion = REQUIRED_API_VERSION;
        request.setMinimumVersion(requiredVersion);
        final ClientInfo clientInfo = new ClientInfo();
        clientInfo.setName(CLIENT_NAME);
        clientInfo.setVersion(CLIENT_VERSION);
        request.setClient(clientInfo);

        try {
            final VersionNegotiationResponse responseBody = versionApi.versionNegotiation(request);
            String message = "The server implements API version " + responseBody.getVersion();
            if (Boolean.TRUE.equals(responseBody.getDeprecated())) {
                message += " but it is deprecated";
            }
            System.out.println(message);
        } catch (ApiException e) {
            if (e.getCode() == 404) {
                throw new RuntimeException("API version " + requiredVersion + " is not supported. The server is too old.");
            }
            if (e.getCode() == 410) {
                throw new RuntimeException("API version " + requiredVersion + " is not supported. The server is too new.");
            }
            throw new RuntimeException("Could not determine server version", exceptionHandler(e));
        }
    }

    public void createAlert(String title, String message) {
        System.out.println("Creating alert...");

        final CreateAlertRequest request = new CreateAlertRequest();
        request.setTitle(title);
        request.setMessage(message);

        final AlertSendTo sendTo = new AlertSendTo();
        sendTo.setAllUsers(true);
        request.setSendTo(sendTo);

        final AlertsApi alertsApi = new AlertsApi(apiClient);
        final CreateAlertResponse response = executeRequest(() -> alertsApi.createAlert(request));
        System.out.println("Alert sent to " + response.getSentToCount() + " users");
    }

    public void recordTextSearch(String expression) {
        if (expression == null || expression.trim().isEmpty()) {
            throw new IllegalArgumentException("Search expression must be specified");
        }
        System.out.println("Executing record text search...");
        final SearchApi searchApi = new SearchApi(apiClient);

        final TextSearchRequest textSearchRequest = new TextSearchRequest();
        textSearchRequest.expression(expression);
        final TextSearchResponse response = executeRequest(() -> searchApi.recordTextSearch(textSearchRequest, OPT_IN_TO_EXPERIMENTAL_API));
        System.out.println("Got " + response.getMatchCount() + " results for query: " + response.getExplain().getFormatted());
        getRecords(response.getRecordGroup());
    }

    public void geoAreaSearch() {
        System.out.println("Executing geospatial search...");
        final SearchApi searchApi = new SearchApi(apiClient);

        final GeospatialSearchRequest geospatialSearchRequest = new GeospatialSearchRequest();
        final FeatureCollection featureCollection = new FeatureCollection();
        featureCollection.setType(FeatureCollection.TypeEnum.FEATURECOLLECTION);
        final Feature feature = new Feature();
        feature.setType(Feature.TypeEnum.FEATURE);
        final GeoJSONPolygon polygon = new GeoJSONPolygon();
        polygon.addCoordinatesItem(listOf(listOf(-77.254486, 38.75087), listOf(-77.254486, 39.030919), listOf(-76.779327, 39.030919), listOf(-76.779327, 38.75087), listOf(-77.254486, 38.75087)));
        feature.setGeometry(polygon);
        featureCollection.addFeaturesItem(feature);
        geospatialSearchRequest.setArea(featureCollection);
        final GeospatialSearchResponse response = executeRequest(() -> searchApi.geospatial(geospatialSearchRequest, OPT_IN_TO_EXPERIMENTAL_API));
        System.out.println("Got " + response.getMatchCount() + " results for query");
        getRecords(response.getRecordGroup());
    }

    // If you are using Java 9+ then you can just use List.of() instead
    private <T> List<T> listOf(T... ts) {
        return Arrays.asList(ts);
    }

    private void getRecords(RecordGroupInfo recordGroup) {
        if (recordGroup == null) {
            System.out.println("No results");
            return;
        }

        System.out.println("Fetching results...");
        final String recordGroupId = recordGroup.getId();
        final Integer recordGroupPageSize = null; // defaults to 500;
        final Set<String> seenRecordIds = new HashSet<>();
        retrieveRecords(recordGroupId, recordGroupPageSize, (records, matchedRecordIds) -> {
            Stream.concat(records.getEntities().stream(), records.getLinks().stream()).forEach(record -> processRecord(matchedRecordIds, seenRecordIds, record));
        });
    }

    private void retrieveRecords(String recordGroupId, Integer pageSize, BiConsumer<Records, Collection<String>> responseConsumer) {
        final RecordGroupsApi recordGroupsApi = new RecordGroupsApi(apiClient);
        String cursor = "*";
        int pageNumber = 1;
        while (cursor != null) {
            System.out.println("Retrieving results page " + pageNumber++);
            final String thisCursor = cursor;
            PagedRecordGroupResponse recordGroupResponse = executeRequest(() -> recordGroupsApi.pagedRecordsResults(recordGroupId, thisCursor, pageSize, null, OPT_IN_TO_EXPERIMENTAL_API));
            Records records = recordGroupResponse.getRecords();
            responseConsumer.accept(records, recordGroupResponse.getMatchedRecordIds());
            PagedRecordsPage nextPage = recordGroupResponse.getNextPage();
            if (nextPage == null) {
                cursor = null;
            } else {
                cursor = nextPage.getCursor();
            }
        }
        executeRequest(() -> { recordGroupsApi.deleteRecordGroup(recordGroupId, OPT_IN_TO_EXPERIMENTAL_API); return null; });
    }

    private void processRecord(Collection<String> matchedRecordIds, Set<String> seenRecordIds, Record record) {
        final String recordId = record.getId();
        // Link ends might have appeared on earlier pages so check if we've seen them before
        if (!seenRecordIds.add(recordId)) {
            return;
        }

        final boolean isLink = Boolean.TRUE.equals(record.getIsLink());
        final String type = (isLink ? linkTypes.get(record.getItemTypeId()).getDisplayName() + " [Link]" : entityTypes.get(record.getItemTypeId()).getDisplayName() + " [Entity]");
        System.out.println("\t" + type);
        System.out.println("\t\tRecord Identifier: " + recordId);
        System.out.println("\t\tLabel: " + record.getLabel());
        System.out.println("\t\tMatched Query: " + (matchedRecordIds.contains(recordId) ? "Yes" : "No (supporting record only)"));
        if (isLink) {
            System.out.println("\t\tFrom - " + record.getFromEnd().getId() + " - " + record.getFromEnd().getLabel());
            System.out.println("\t\tTo - " + record.getToEnd().getId() + " - " + record.getToEnd().getLabel());
        }
        System.out.println("\t\t-------------");
        record.getProperties().forEach(this::processProperty);
        System.out.println();
    }

    private void processProperty(RecordPropertyBase property) {
        final String value;
        switch (property.getLogicalType()) {
            case SINGLE_LINE_STRING:
            case MULTIPLE_LINE_STRING:
            case SELECTED_FROM:
            case SUGGESTED_FROM:
                value = ((RecordStringProperty) property).getValue();
                break;
            case DATE:
                value = ((RecordDateProperty) property).getValue().toString();
                break;
            case TIME:
                value = ((RecordTimeProperty) property).getValue();
                break;
            case DATE_AND_TIME:
                final DateAndTime dateAndTime = ((RecordDateAndTimeProperty) property).getValue();
                value = dateAndTime.getLocalDateAndTime() + " " + dateAndTime.getTimeZoneId();
                break;
            case BOOLEAN:
                value = ((RecordBooleanProperty) property).getValue().toString();
                break;
            case INTEGER:
                value = ((RecordIntegerProperty) property).getValue().toString();
                break;
            case DOUBLE:
                value = ((RecordDoubleProperty) property).getValue().toString();
                break;
            case DECIMAL:
                value = ((RecordDecimalProperty) property).getValue().toString();
                break;
            case GEOSPATIAL:
                final RecordGeospatialProperty geoProp = (RecordGeospatialProperty) property;
                
                // Currently records only have Point properties, but that might change in the future without the REST API changing.
                // It is safer to check rather than cast directly to GeoJSONPoint.
                if (geoProp.getValue().getType() == Geometry.TypeEnum.POINT) {
                    final GeoJSONPoint point = (GeoJSONPoint) geoProp.getValue();
                    final List<Double> coordinates = point.getCoordinates();
                    value = String.format("View on a map: https://www.openstreetmap.org/?mlon=%s&mlat=%s&zoom=15", coordinates.get(0), coordinates.get(1));
                } else {
                    value = geoProp.getValue().toString();
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown property type " + property.getLogicalType());
        }
        System.out.println("\t\t" + propertyTypes.get(property.getTypeId()).getDisplayName() + " - " + value);
    }

    @FunctionalInterface
    private interface IApiRequest<T> {
        T execute() throws ApiException;
    }

    private <T> T executeRequest(IApiRequest<T> apiRequest) {
        try {
            return apiRequest.execute();
        } catch (ApiException e) {
            throw exceptionHandler(e);
        }
    }

    private RuntimeException exceptionHandler(ApiException e) {
        final List<String> contentTypes = e.getResponseHeaders().get("Content-Type");
        if (contentTypes.contains("application/problem+json")) {
            try {
                final ObjectMapper objectMapper = apiClient.getObjectMapper();
                final I2RFC7807ProblemDetails problemDetails = objectMapper.readValue(e.getResponseBody(), I2RFC7807ProblemDetails.class);

                final String type = problemDetails.getType().toString();
                final boolean isUserProblem = type.equals("tag:i2group.com,2022:user-problem-details");

                final String summary;
                if (isUserProblem) {
                    summary = "Bad user input";
                } else if (problemDetails.getStatus() >= 400 && problemDetails.getStatus() < 500) {
                    summary = "Bad API request";
                } else {
                    summary = "Server error";
                }

                String errorMessage = summary;
                if (problemDetails.getErrors() != null && !problemDetails.getErrors().isEmpty()) {
                    String reason = problemDetails.getErrors().stream().map(RFC7807ErrorDescription::getMessage).filter(Objects::nonNull).collect(Collectors.joining(", "));
                    if (reason.isEmpty()) {
                        reason = problemDetails.getDetail();
                    }
                    if (reason.isEmpty()) {
                        reason = problemDetails.getTitle();
                    }
                    errorMessage = errorMessage.concat(": ").concat(reason);
                }

                throw new RuntimeException(errorMessage, e);
            } catch (JsonProcessingException e2) {
                throw new RuntimeException(e2);
            }
        }
        return new RuntimeException("Error - status: " + e.getCode(), e);
    }

    public static void main(String[] args) {
        final MyApiClient myApiClient = new MyApiClient(SERVER_URL, "Jenny", "Jenny");

        final ExampleApp app = new ExampleApp(myApiClient);
        app.createAlert("Hello, I'm a REST API client", String.format("(name: %s, version: %s)", CLIENT_NAME, CLIENT_VERSION));
        app.recordTextSearch("(gene hendricks) OR Associate");
        app.geoAreaSearch();
    }
}
