create table TEST_TABLE
(
    TEST_NAME              varchar(50) primary key,
    TEST_VALUE             varchar(50),
    LAST_UPDATED_TIMESTAMP timestamp(8) not null
);

create index TEST_TABLE_VALUE_IDX on TEST_TABLE(TEST_VALUE);
