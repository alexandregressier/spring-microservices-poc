-- Preloaded databases
DROP DATABASE defaultdb;
DROP DATABASE postgres;

-- OSP Organization Service
CREATE DATABASE osp_organization_service;
CREATE USER osp_organization_service;
GRANT ALL ON DATABASE osp_organization_service TO osp_organization_service;

-- OSP License Service
CREATE DATABASE osp_license_service;
CREATE USER osp_license_service;
GRANT ALL ON DATABASE osp_license_service TO osp_license_service;