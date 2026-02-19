# Customer Rewards Program Application
Customer Rewards Program application provides an API for 
calculating the 
rewards 
points earned per month and total. 

### Customer Rewards Points API Technical Details
RESTful Endpoint URL: http://localhost:8080/customer-rewards/points/{customerId}?rewardsPeriodType={rewardsPeriodType}&rewardsPeriod={rewardsPeriod}

Request Method: GET
#### API Request Parameters:
* customerId (Path Param) (Required): Customer ID
* rewardsPeriodType (Query Param) (Optional) (Default : 'MONTH') : Rewards period type - currently API supports only 'MONTH'
* rewardsPeriod (Query Param) (Optional) (Default: 3) (Min: 1, Max: 240): Rewards period in months

#### API Response:
Upon successful response, the API returns the following details:
* customer details: ID, Name and Phone number
* rewards points: Points earned per month and total

Sample JSON Response Body:

{
"customerId": 1,
"personalInfo": {
"customerName": "Test Name 1",
"phoneNumber": "1000000001"
},
"rewards": {
"description": "Customer Rewards Points per month",
"rewardsPeriodType": "MONTH",
"monthlyPointsList": [
{
"points": 0,
"month": 11,
"year": 2025
},
{
"points": 70,
"month": 12,
"year": 2025
},
{
"points": 450,
"month": 1,
"year": 2026
}
],
"totalPoints": 520,
"totalAmount": 460.70
}
}

### Tech stack
* Java 17
* Spring Boot 4.0.2
* Tomcat
* Maven

### Customer Rewards Points API Design Details
Though the API currently supports calculating only points on monthly basis, it could be
enhanced to support calculating it on daily/yearly/etc., basis.

#### Request parameter 'rewardsPeriod'
The API calculates points based on the transactions recorded in last 'rewardsPeriod' 
months. The maximum value of 'rewardsPeriod' the API supports is 240, i.e., the API can only calculate points
on monthly basis for upto last 240 months. The minimum value of 'rewardsPeriod' is 1.  

Example: If 'rewardsPeriod' input in the request is 3 and the request was made in the 
month of January 2026, then API calculates the points earned during last 3 months i.e.,
October 2025 - November 2025. 