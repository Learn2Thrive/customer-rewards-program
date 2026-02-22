## Customer Rewards Points API Request/Response:

### Get customer rewards points for last 3 months

    Request:
    curl --location 'http://localhost:8080/customer-rewards/points/1?rewardsPeriodType=MONTH&rewardsPeriod=3' \
    --header 'Content-Type: application/json'

    Response (200 OK):
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


### Get customer rewards points without 'rewardsPeriod' & 'rewardsPeriodType' request params.

    Request:
    curl --location 'http://localhost:8080/customer-rewards/points/1' \
    --header 'Content-Type: application/json'

    Response (200 OK):
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

### Get customer rewards points for customer without any purchase orders in last 3 months

    Request:
    curl --location 'http://localhost:8080/customer-rewards/points/4?rewardsPeriodType=MONTH&rewardsPeriod=3' \
    --header 'Content-Type: application/json'

    Response (200 OK):
    {
        "customerId": 4,
        "personalInfo": {
            "customerName": "Test Name 4",
            "phoneNumber": "1000000004"
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
                    "points": 0,
                    "month": 12,
                    "year": 2025
                },
                {
                    "points": 0,
                    "month": 1,
                    "year": 2026
                }
            ],
            "totalPoints": 0,
            "totalAmount": 0
        }
    }

### Get customer rewards points for invalid customer

    Request:
    curl --location 'http://localhost:8080/customer-rewards/points/5?rewardsPeriodType=MONTH&rewardsPeriod=3' \
    --header 'Content-Type: application/json'

    Response (400 Bad Request):
    {
        "errorMessage": "Customer with id: 5 not found",
        "status": 400
    }