drop table build_transaction_history;

create table build_transaction_history (
BUILD_NUMBER                              VARCHAR2(50) NOT NULL ,
PATCH_LEVEL                               VARCHAR2(50),
TRANSACTION_TYPE                           VARCHAR2(10) NOT NULL ,
NAME                                       VARCHAR2(200) NOT NULL ,
STATUS                                     VARCHAR2(15) NOT NULL ,
SUBMITTER                                  VARCHAR2(100) NOT NULL ,
DATE_EXECUTED                              DATE  default sysdate,
TRANSACTION_PARAMS                         VARCHAR2(1200));