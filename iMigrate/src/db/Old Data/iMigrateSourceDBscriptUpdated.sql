USE [master]
GO
/****** Object:  Database [iMigrateSourcedb]    Script Date: 4/20/2024 4:01:40 AM ******/
CREATE DATABASE [iMigrateSourcedb]
 CONTAINMENT = NONE
 ON  PRIMARY 
( NAME = N'iMigrateSourcedb', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\iMigrateSourcedb.mdf' , SIZE = 8192KB , MAXSIZE = UNLIMITED, FILEGROWTH = 65536KB )
 LOG ON 
( NAME = N'iMigrateSourcedb_log', FILENAME = N'C:\Program Files\Microsoft SQL Server\MSSQL16.MSSQLSERVER\MSSQL\DATA\iMigrateSourcedb_log.ldf' , SIZE = 8192KB , MAXSIZE = 2048GB , FILEGROWTH = 65536KB )
 WITH CATALOG_COLLATION = DATABASE_DEFAULT, LEDGER = OFF
GO
ALTER DATABASE [iMigrateSourcedb] SET COMPATIBILITY_LEVEL = 160
GO
IF (1 = FULLTEXTSERVICEPROPERTY('IsFullTextInstalled'))
begin
EXEC [iMigrateSourcedb].[dbo].[sp_fulltext_database] @action = 'enable'
end
GO
ALTER DATABASE [iMigrateSourcedb] SET ANSI_NULL_DEFAULT OFF 
GO
ALTER DATABASE [iMigrateSourcedb] SET ANSI_NULLS OFF 
GO
ALTER DATABASE [iMigrateSourcedb] SET ANSI_PADDING OFF 
GO
ALTER DATABASE [iMigrateSourcedb] SET ANSI_WARNINGS OFF 
GO
ALTER DATABASE [iMigrateSourcedb] SET ARITHABORT OFF 
GO
ALTER DATABASE [iMigrateSourcedb] SET AUTO_CLOSE OFF 
GO
ALTER DATABASE [iMigrateSourcedb] SET AUTO_SHRINK OFF 
GO
ALTER DATABASE [iMigrateSourcedb] SET AUTO_UPDATE_STATISTICS ON 
GO
ALTER DATABASE [iMigrateSourcedb] SET CURSOR_CLOSE_ON_COMMIT OFF 
GO
ALTER DATABASE [iMigrateSourcedb] SET CURSOR_DEFAULT  GLOBAL 
GO
ALTER DATABASE [iMigrateSourcedb] SET CONCAT_NULL_YIELDS_NULL OFF 
GO
ALTER DATABASE [iMigrateSourcedb] SET NUMERIC_ROUNDABORT OFF 
GO
ALTER DATABASE [iMigrateSourcedb] SET QUOTED_IDENTIFIER OFF 
GO
ALTER DATABASE [iMigrateSourcedb] SET RECURSIVE_TRIGGERS OFF 
GO
ALTER DATABASE [iMigrateSourcedb] SET  DISABLE_BROKER 
GO
ALTER DATABASE [iMigrateSourcedb] SET AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO
ALTER DATABASE [iMigrateSourcedb] SET DATE_CORRELATION_OPTIMIZATION OFF 
GO
ALTER DATABASE [iMigrateSourcedb] SET TRUSTWORTHY OFF 
GO
ALTER DATABASE [iMigrateSourcedb] SET ALLOW_SNAPSHOT_ISOLATION OFF 
GO
ALTER DATABASE [iMigrateSourcedb] SET PARAMETERIZATION SIMPLE 
GO
ALTER DATABASE [iMigrateSourcedb] SET READ_COMMITTED_SNAPSHOT OFF 
GO
ALTER DATABASE [iMigrateSourcedb] SET HONOR_BROKER_PRIORITY OFF 
GO
ALTER DATABASE [iMigrateSourcedb] SET RECOVERY FULL 
GO
ALTER DATABASE [iMigrateSourcedb] SET  MULTI_USER 
GO
ALTER DATABASE [iMigrateSourcedb] SET PAGE_VERIFY CHECKSUM  
GO
ALTER DATABASE [iMigrateSourcedb] SET DB_CHAINING OFF 
GO
ALTER DATABASE [iMigrateSourcedb] SET FILESTREAM( NON_TRANSACTED_ACCESS = OFF ) 
GO
ALTER DATABASE [iMigrateSourcedb] SET TARGET_RECOVERY_TIME = 60 SECONDS 
GO
ALTER DATABASE [iMigrateSourcedb] SET DELAYED_DURABILITY = DISABLED 
GO
ALTER DATABASE [iMigrateSourcedb] SET ACCELERATED_DATABASE_RECOVERY = OFF  
GO
EXEC sys.sp_db_vardecimal_storage_format N'iMigrateSourcedb', N'ON'
GO
ALTER DATABASE [iMigrateSourcedb] SET QUERY_STORE = ON
GO
ALTER DATABASE [iMigrateSourcedb] SET QUERY_STORE (OPERATION_MODE = READ_WRITE, CLEANUP_POLICY = (STALE_QUERY_THRESHOLD_DAYS = 30), DATA_FLUSH_INTERVAL_SECONDS = 900, INTERVAL_LENGTH_MINUTES = 60, MAX_STORAGE_SIZE_MB = 1000, QUERY_CAPTURE_MODE = AUTO, SIZE_BASED_CLEANUP_MODE = AUTO, MAX_PLANS_PER_QUERY = 200, WAIT_STATS_CAPTURE_MODE = ON)
GO
USE [iMigrateSourcedb]
GO
/****** Object:  Table [dbo].[iMigrateEmpDetails]    Script Date: 4/20/2024 4:01:40 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[iMigrateEmpDetails](
	[EmpId] [numeric](18, 0) NOT NULL,
	[EmpName] [nvarchar](50) NOT NULL,
	[EmpCompany] [nvarchar](50) NULL,
	[EmpPhoneNumber] [numeric](18, 0) NULL,
	[EmpAddress] [nvarchar](50) NULL,
	[EmpYOE] [int] NULL,
	[RecordCreateDt] [datetime] NOT NULL,
	[RecordUpdateDt] [datetime] NOT NULL,
 CONSTRAINT [PK_iMigrateEmpDetails] PRIMARY KEY CLUSTERED 
(
	[EmpId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  View [dbo].[iMigrateEmpView]    Script Date: 4/20/2024 4:01:40 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE VIEW [dbo].[iMigrateEmpView]
AS
SELECT        EmpName, EmpCompany, EmpId
FROM            dbo.iMigrateEmpDetails
GO
/****** Object:  UserDefinedFunction [dbo].[GetEmpNameByEmpId]    Script Date: 4/20/2024 4:01:40 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE FUNCTION [dbo].[GetEmpNameByEmpId] 
(	
	@EmployeeId nvarchar(50) = NULL
)
RETURNS TABLE 
AS
RETURN 
(
	-- Add the SELECT statement with parameter references here
	SELECT EmpId,EmpName from dbo.iMigrateEmpDetails where
	EmpId=@EmployeeId
)
GO
/****** Object:  Table [dbo].[iMigrateAgents]    Script Date: 4/20/2024 4:01:40 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[iMigrateAgents](
	[AGENT_CODE] [char](6) NOT NULL,
	[AGENT_NAME] [char](40) NULL,
	[WORKING_AREA] [char](35) NULL,
	[COMMISSION] [decimal](10, 2) NULL,
	[PHONE_NO] [char](15) NULL,
	[COUNTRY] [nvarchar](25) NULL,
PRIMARY KEY CLUSTERED 
(
	[AGENT_CODE] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[iMigrateCustomerDetails]    Script Date: 4/20/2024 4:01:40 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[iMigrateCustomerDetails](
	[CUST_CODE] [nvarchar](6) NOT NULL,
	[CUST_NAME] [nvarchar](40) NOT NULL,
	[CUST_CITY] [char](35) NULL,
	[WORKING_AREA] [nvarchar](35) NOT NULL,
	[CUST_COUNTRY] [nvarchar](20) NOT NULL,
	[GRADE] [int] NULL,
	[OPENING_AMT] [decimal](12, 2) NOT NULL,
	[RECEIVE_AMT] [decimal](12, 2) NOT NULL,
	[PAYMENT_AMT] [decimal](12, 2) NOT NULL,
	[OUTSTANDING_AMT] [decimal](12, 2) NOT NULL,
	[PHONE_NO] [nvarchar](17) NOT NULL,
	[AGENT_CODE] [char](6) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[CUST_CODE] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[iMigrateOrders]    Script Date: 4/20/2024 4:01:40 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[iMigrateOrders](
	[ORD_NUM] [numeric](6, 0) NOT NULL,
	[ORD_AMOUNT] [decimal](12, 2) NOT NULL,
	[ADVANCE_AMOUNT] [decimal](12, 2) NOT NULL,
	[ORD_DATE] [date] NOT NULL,
	[CUST_CODE] [nvarchar](6) NOT NULL,
	[AGENT_CODE] [char](6) NOT NULL,
	[ORD_DESCRIPTION] [nvarchar](60) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[ORD_NUM] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[iMigrateCustomerDetails]  WITH CHECK ADD FOREIGN KEY([AGENT_CODE])
REFERENCES [dbo].[iMigrateAgents] ([AGENT_CODE])
GO
ALTER TABLE [dbo].[iMigrateOrders]  WITH CHECK ADD FOREIGN KEY([AGENT_CODE])
REFERENCES [dbo].[iMigrateAgents] ([AGENT_CODE])
GO
ALTER TABLE [dbo].[iMigrateOrders]  WITH CHECK ADD FOREIGN KEY([CUST_CODE])
REFERENCES [dbo].[iMigrateCustomerDetails] ([CUST_CODE])
GO
/****** Object:  StoredProcedure [dbo].[empGetYearOfExp]    Script Date: 4/20/2024 4:01:40 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<iMigrate>
-- Create date: <19/04/2024>
-- Description:	<This stored procedure is to return the YOE of an Employee>
-- =============================================
CREATE PROCEDURE [dbo].[empGetYearOfExp]
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
EXEC sys.sp_addextendedproperty @name=N'MS_DiagramPane1', @value=N'[0E232FF0-B466-11cf-A24F-00AA00A3EFFF, 1.00]
Begin DesignProperties = 
   Begin PaneConfigurations = 
      Begin PaneConfiguration = 0
         NumPanes = 4
         Configuration = "(H (1[42] 4[28] 2[14] 3) )"
      End
      Begin PaneConfiguration = 1
         NumPanes = 3
         Configuration = "(H (1 [50] 4 [25] 3))"
      End
      Begin PaneConfiguration = 2
         NumPanes = 3
         Configuration = "(H (1 [50] 2 [25] 3))"
      End
      Begin PaneConfiguration = 3
         NumPanes = 3
         Configuration = "(H (4 [30] 2 [40] 3))"
      End
      Begin PaneConfiguration = 4
         NumPanes = 2
         Configuration = "(H (1 [56] 3))"
      End
      Begin PaneConfiguration = 5
         NumPanes = 2
         Configuration = "(H (2 [66] 3))"
      End
      Begin PaneConfiguration = 6
         NumPanes = 2
         Configuration = "(H (4 [50] 3))"
      End
      Begin PaneConfiguration = 7
         NumPanes = 1
         Configuration = "(V (3))"
      End
      Begin PaneConfiguration = 8
         NumPanes = 3
         Configuration = "(H (1[56] 4[18] 2) )"
      End
      Begin PaneConfiguration = 9
         NumPanes = 2
         Configuration = "(H (1 [75] 4))"
      End
      Begin PaneConfiguration = 10
         NumPanes = 2
         Configuration = "(H (1[66] 2) )"
      End
      Begin PaneConfiguration = 11
         NumPanes = 2
         Configuration = "(H (4 [60] 2))"
      End
      Begin PaneConfiguration = 12
         NumPanes = 1
         Configuration = "(H (1) )"
      End
      Begin PaneConfiguration = 13
         NumPanes = 1
         Configuration = "(V (4))"
      End
      Begin PaneConfiguration = 14
         NumPanes = 1
         Configuration = "(V (2))"
      End
      ActivePaneConfig = 0
   End
   Begin DiagramPane = 
      Begin Origin = 
         Top = 0
         Left = 0
      End
      Begin Tables = 
         Begin Table = "iMigrateEmpDetails"
            Begin Extent = 
               Top = 6
               Left = 38
               Bottom = 136
               Right = 229
            End
            DisplayFlags = 280
            TopColumn = 0
         End
      End
   End
   Begin SQLPane = 
   End
   Begin DataPane = 
      Begin ParameterDefaults = ""
      End
   End
   Begin CriteriaPane = 
      Begin ColumnWidths = 11
         Column = 1440
         Alias = 900
         Table = 1170
         Output = 720
         Append = 1400
         NewValue = 1170
         SortType = 1350
         SortOrder = 1410
         GroupBy = 1350
         Filter = 1350
         Or = 1350
         Or = 1350
         Or = 1350
      End
   End
End
' , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'iMigrateEmpView'
GO
EXEC sys.sp_addextendedproperty @name=N'MS_DiagramPaneCount', @value=1 , @level0type=N'SCHEMA',@level0name=N'dbo', @level1type=N'VIEW',@level1name=N'iMigrateEmpView'
GO
USE [master]
GO
ALTER DATABASE [iMigrateSourcedb] SET  READ_WRITE 
GO
