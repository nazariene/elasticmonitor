# General

Small app that "monitors" elasticsearch and reports issues.
Monitoring is done via a set of user-configured rules that are periodically run against ES indices.
Rules have conditions - that, when breached, trigger an alarm.
Alarm actions are then executed - such as sending an email or messaging to telegram.


#Rules
Rule is is a yaml file with several blocks:
## name - unique, arbitrary rule name
## trigger - when rule is triggered.
Supports 2 values: 
  - cron - runs a rule by cron schedule
  - interval - runs a rule periodically, seconds
If both are specified - cron takes precedence.
E.g.:

  #cron: "0 4,34 * ? * * *"
  interval: 1900              #Interval in seconds

## query - query to fire against ES
  - index - which index to query
  - value - json with ES request body


## condition - condition to check "query" against. I.e. "query" results are compared against this condition to check if condition was breached

condition:
  field: "/hits/total/value"     #field in query json response to check against
  fieldType: int                 #field type of json field. Supported values: int, boolean, double, float, long, string
  op: eq                         #comparison operation of "field". Supported values: eq, gte, gt, lt, lte
  value: 0                       #value to check "field" against using "op" operator. Will be casted to type specified in "fieldType"

## action - action to execute if condition was breached while running a rule
Supported values are: email, telegram, log (set via "type" field)

Email action supports multiple recipients.

You can dynamically insert values from the rule (including results you get from ES query) into the alert message (see example).
To include env variable: {{[env:VARIABLE_NAME]}}
To include query result: {{[search:PATH_TO_RESULT]}}
To include values from rule itself: {{query.index}} or {{name}}

Examples: 

action:
  - name: Email to me
    type: email
    to:
      - test@test.com
      - test2@test.com
    subject: "ERROR: Blah blah!"
    body: "Found {{[search:/hits/total/value]}} under {{query.index}} index in {{[env:elasticsearch.host]}}\n"

  - name: Log alert to console
    type: log
    message: "\u2757 \u2757 \u2757 \u2757 \n
              Found {{[search:/hits/total/value]}} under {{query.index}} index in {{[env:elasticsearch.host]}}\n
                          \u2757 \u2757 \u2757 \u2757"

  - name: Send message to telegram channel
    type: telegram
    channelId: -452773100
    message: "\u2757 \u2757 \u2757 \u2757 \n
              Found {{[search:/hits/total/value]}} under {{query.index}} index in {{[env:elasticsearch.host]}}\n
                          \u2757 \u2757 \u2757 \u2757"


# Deployment
You can run it as a standalone spring-boot java application or deploy it to k8s (chart is located under "chart" folder).