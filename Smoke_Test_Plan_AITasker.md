# AITasker Smoke Test Plan

## Purpose

Verify Marketplace Core APIs.

## Environment

-   Spring Boot running
-   SQL Server
-   CLIENT/EXPERT/ADMIN accounts

## TC-01: POST /api/auth/register

**Objective** - Register user

**Preconditions** - Server running - JWT available if required

**Request** - Method: `POST` - Endpoint: `/api/auth/register`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-02: POST /api/auth/login

**Objective** - Login

**Preconditions** - Server running - JWT available if required

**Request** - Method: `POST` - Endpoint: `/api/auth/login`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-03: GET /api/users/me

**Objective** - Current user

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint: `/api/users/me`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-04: GET /api/experts/me

**Objective** - Get expert profile

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint: `/api/experts/me`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-05: PUT /api/experts/me

**Objective** - Update expert profile

**Preconditions** - Server running - JWT available if required

**Request** - Method: `PUT` - Endpoint: `/api/experts/me`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-06: POST /api/experts/portfolio

**Objective** - Create portfolio

**Preconditions** - Server running - JWT available if required

**Request** - Method: `POST` - Endpoint: `/api/experts/portfolio`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-07: GET /api/experts/portfolio/{expertId}

**Objective** - Get portfolio

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint:
`/api/experts/portfolio/{expertId}`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-08: DELETE /api/experts/portfolio/{id}

**Objective** - Delete portfolio

**Preconditions** - Server running - JWT available if required

**Request** - Method: `DELETE` - Endpoint: `/api/experts/portfolio/{id}`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-09: POST /api/services

**Objective** - Create service package

**Preconditions** - Server running - JWT available if required

**Request** - Method: `POST` - Endpoint: `/api/services`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-10: GET /api/services

**Objective** - List service packages

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint: `/api/services`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-11: PUT /api/services/{id}

**Objective** - Update service package

**Preconditions** - Server running - JWT available if required

**Request** - Method: `PUT` - Endpoint: `/api/services/{id}`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-12: DELETE /api/services/{id}

**Objective** - Delete service package

**Preconditions** - Server running - JWT available if required

**Request** - Method: `DELETE` - Endpoint: `/api/services/{id}`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-13: POST /api/jobs

**Objective** - Create job

**Preconditions** - Server running - JWT available if required

**Request** - Method: `POST` - Endpoint: `/api/jobs`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-14: GET /api/jobs

**Objective** - List jobs

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint: `/api/jobs`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-15: GET /api/jobs/{id}

**Objective** - Get job

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint: `/api/jobs/{id}`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-16: GET /api/jobs/search

**Objective** - Search jobs

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint: `/api/jobs/search`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-17: GET /api/jobs/me

**Objective** - My jobs

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint: `/api/jobs/me`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-18: PUT /api/jobs/{id}

**Objective** - Update job

**Preconditions** - Server running - JWT available if required

**Request** - Method: `PUT` - Endpoint: `/api/jobs/{id}`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-19: DELETE /api/jobs/{id}

**Objective** - Delete job

**Preconditions** - Server running - JWT available if required

**Request** - Method: `DELETE` - Endpoint: `/api/jobs/{id}`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-20: POST /api/proposals

**Objective** - Create proposal

**Preconditions** - Server running - JWT available if required

**Request** - Method: `POST` - Endpoint: `/api/proposals`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-21: GET /api/proposals/job/{jobId}

**Objective** - List job proposals

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint: `/api/proposals/job/{jobId}`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-22: GET /api/proposals/me

**Objective** - My proposals

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint: `/api/proposals/me`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-23: PUT /api/proposals/{id}/accept

**Objective** - Accept proposal

**Preconditions** - Server running - JWT available if required

**Request** - Method: `PUT` - Endpoint: `/api/proposals/{id}/accept`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-24: PUT /api/proposals/{id}/reject

**Objective** - Reject proposal

**Preconditions** - Server running - JWT available if required

**Request** - Method: `PUT` - Endpoint: `/api/proposals/{id}/reject`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-25: PUT /api/proposals/{id}/withdraw

**Objective** - Withdraw proposal

**Preconditions** - Server running - JWT available if required

**Request** - Method: `PUT` - Endpoint: `/api/proposals/{id}/withdraw`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-26: POST /api/projects

**Objective** - Create project

**Preconditions** - Server running - JWT available if required

**Request** - Method: `POST` - Endpoint: `/api/projects`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-27: GET /api/projects/{id}

**Objective** - Project detail

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint: `/api/projects/{id}`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-28: GET /api/projects/me

**Objective** - My projects

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint: `/api/projects/me`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-29: GET /api/projects/client

**Objective** - Client projects

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint: `/api/projects/client`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-30: GET /api/projects/expert

**Objective** - Expert projects

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint: `/api/projects/expert`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-31: PUT /api/projects/{id}

**Objective** - Update project

**Preconditions** - Server running - JWT available if required

**Request** - Method: `PUT` - Endpoint: `/api/projects/{id}`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-32: POST /api/milestones

**Objective** - Create milestone

**Preconditions** - Server running - JWT available if required

**Request** - Method: `POST` - Endpoint: `/api/milestones`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-33: GET /api/milestones/project/{projectId}

**Objective** - Project milestones

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint:
`/api/milestones/project/{projectId}`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-34: PUT /api/milestones/{id}

**Objective** - Update milestone

**Preconditions** - Server running - JWT available if required

**Request** - Method: `PUT` - Endpoint: `/api/milestones/{id}`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-35: PUT /api/milestones/{id}/submit

**Objective** - Submit milestone

**Preconditions** - Server running - JWT available if required

**Request** - Method: `PUT` - Endpoint: `/api/milestones/{id}/submit`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-36: PUT /api/milestones/{id}/approve

**Objective** - Approve milestone

**Preconditions** - Server running - JWT available if required

**Request** - Method: `PUT` - Endpoint: `/api/milestones/{id}/approve`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-37: PUT /api/milestones/{id}/reject

**Objective** - Reject milestone

**Preconditions** - Server running - JWT available if required

**Request** - Method: `PUT` - Endpoint: `/api/milestones/{id}/reject`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-38: PUT /api/milestones/{id}/release-payment

**Objective** - Release payment

**Preconditions** - Server running - JWT available if required

**Request** - Method: `PUT` - Endpoint:
`/api/milestones/{id}/release-payment`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-39: POST /api/deliveries

**Objective** - Submit delivery

**Preconditions** - Server running - JWT available if required

**Request** - Method: `POST` - Endpoint: `/api/deliveries`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-40: GET /api/deliveries/milestone/{id}

**Objective** - Delivery history

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint: `/api/deliveries/milestone/{id}`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-41: POST /api/payments/deposit

**Objective** - Deposit escrow

**Preconditions** - Server running - JWT available if required

**Request** - Method: `POST` - Endpoint: `/api/payments/deposit`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-42: PUT /api/payments/release

**Objective** - Release payment

**Preconditions** - Server running - JWT available if required

**Request** - Method: `PUT` - Endpoint: `/api/payments/release`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-43: GET /api/payments/transactions/{paymentId}

**Objective** - Payment transactions

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint:
`/api/payments/transactions/{paymentId}`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-44: POST /api/withdrawals

**Objective** - Create withdrawal

**Preconditions** - Server running - JWT available if required

**Request** - Method: `POST` - Endpoint: `/api/withdrawals`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-45: GET /api/withdrawals/me

**Objective** - My withdrawals

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint: `/api/withdrawals/me`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-46: GET /api/admin/payments/withdrawals

**Objective** - Admin withdrawal list

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint:
`/api/admin/payments/withdrawals`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-47: PUT /api/admin/payments/withdrawals/{id}/approve

**Objective** - Approve withdrawal

**Preconditions** - Server running - JWT available if required

**Request** - Method: `PUT` - Endpoint:
`/api/admin/payments/withdrawals/{id}/approve`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-48: GET /api/admin/payments/transactions

**Objective** - All transactions

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint:
`/api/admin/payments/transactions`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-49: POST /api/reviews

**Objective** - Create review

**Preconditions** - Server running - JWT available if required

**Request** - Method: `POST` - Endpoint: `/api/reviews`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-50: GET /api/reviews/user/{id}

**Objective** - User reviews

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint: `/api/reviews/user/{id}`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-51: GET /api/notifications

**Objective** - Notifications

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint: `/api/notifications`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-52: PUT /api/notifications/{id}/read

**Objective** - Read notification

**Preconditions** - Server running - JWT available if required

**Request** - Method: `PUT` - Endpoint: `/api/notifications/{id}/read`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-53: GET /api/messages/project/{projectId}

**Objective** - Project messages

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint:
`/api/messages/project/{projectId}`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-54: GET /api/admin/users

**Objective** - Admin users

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint: `/api/admin/users`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.

## TC-55: GET /api/admin/test

**Objective** - Admin test

**Preconditions** - Server running - JWT available if required

**Request** - Method: `GET` - Endpoint: `/api/admin/test`

**Headers** - Authorization: Bearer `<JWT>`{=html} (protected APIs) -
Content-Type: application/json

**Expected** - Success status (200/201 as designed) - Valid response
payload - No 500 error

**Database Checks** - Data persisted/updated correctly - Foreign keys
remain valid

**Security Checks** - No JWT -\> 401 (protected) - Wrong role -\> 403 -
Invalid resource -\> 404 where applicable

**Negative Tests** - Invalid payload - Missing required field - Invalid
ownership

**PASS** - Endpoint behaves according to specification.
