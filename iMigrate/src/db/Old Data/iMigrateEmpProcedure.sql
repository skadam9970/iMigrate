-- ================================================
-- Template generated from Template Explorer using:
-- Create Procedure (New Menu).SQL
--
-- Use the Specify Values for Template Parameters 
-- command (Ctrl-Shift-M) to fill in the parameter 
-- values below.
--
-- This block of comments will not be included in
-- the definition of the procedure.
-- ================================================
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<iMigrate>
-- Create date: <19/04/2024>
-- Description:	<This stored procedure is to return the YOE of an Employee>
-- =============================================
CREATE PROCEDURE dbo.empGetYearOfExp
	-- Add the parameters for the stored procedure here
	@EmployeeId nvarchar(50) = NULL
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	SELECT EmpYOE from dbo.iMigrateEmpDetails where 
	EmpId=@EmployeeId
END
GO
