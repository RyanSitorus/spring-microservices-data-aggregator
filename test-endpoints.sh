#!/bin/bash
set -e

BASE_URL="http://localhost:8080"

echo "Running API Gateway and Microservices Verification Tests..."
echo "Target Base URL: $BASE_URL"
echo "--------------------------------------------------------"

# 1. Health check
echo -n "Checking Gateway Health: "
STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/actuator/health")
if [ "$STATUS" -eq 200 ]; then
    echo "OK (HTTP 200)"
else
    echo "FAILED (HTTP $STATUS)"
    exit 1
fi

# 2. JSON Aggregation - Single user
echo -n "Checking JSON Aggregator (/api/v1/aggregate/1): "
JSON_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/v1/aggregate/1")
if [ "$JSON_STATUS" -eq 200 ]; then
    echo "OK (HTTP 200)"
else
    echo "FAILED (HTTP $JSON_STATUS)"
    exit 1
fi

# 3. JSON Aggregation - All users
echo -n "Checking JSON Aggregator (/api/v1/aggregate): "
ALL_JSON_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/v1/aggregate")
if [ "$ALL_JSON_STATUS" -eq 200 ]; then
    echo "OK (HTTP 200)"
else
    echo "FAILED (HTTP $ALL_JSON_STATUS)"
    exit 1
fi

# 4. XML Feed - User filtered
echo -n "Checking XML Feed (/api/v1/posts/xml?userId=1): "
XML_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/v1/posts/xml?userId=1")
if [ "$XML_STATUS" -eq 200 ]; then
    echo "OK (HTTP 200)"
else
    echo "FAILED (HTTP $XML_STATUS)"
    exit 1
fi

# 5. XML Feed - All posts
echo -n "Checking XML Feed (/api/v1/posts/xml): "
ALL_XML_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/api/v1/posts/xml")
if [ "$ALL_XML_STATUS" -eq 200 ]; then
    echo "OK (HTTP 200)"
else
    echo "FAILED (HTTP $ALL_XML_STATUS)"
    exit 1
fi

echo "--------------------------------------------------------"
echo "All endpoint verification tests passed successfully."
