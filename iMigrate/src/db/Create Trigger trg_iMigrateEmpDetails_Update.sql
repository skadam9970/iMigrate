Use iMigrateSourcedb
go 

CREATE TRIGGER trg_iMigrateEmpDetails_Update
ON dbo.iMigrateEmpDetails
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Check if EmpName column has been updated
    IF UPDATE(EmpName)
    BEGIN
        PRINT 'EmpName column updated.';
        -- Your trigger logic here for when EmpName column is updated
    END

    -- Check if EmpCompany column has been updated
    IF UPDATE(EmpCompany)
    BEGIN
        PRINT 'EmpCompany column updated.';
        -- Your trigger logic here for when EmpCompany column is updated
    END

    -- Add more conditions as needed for other columns

END;
