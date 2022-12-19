import React from 'react';
import ReactTable from 'react-table';

export const Table = ({ data }) => {
  return (
    <ReactTable
      data={data}
      columns={[
        {
          Header: 'HOST',
          accessor: 'host'
        },
        {
          Header: 'VERSION',
          accessor: 'version'
        },
        {
          Header: 'ADMIN',
          accessor: 'admin'
        },
        {
          Header: 'DHCP',
          accessor: 'dhcp'
        },
        {
          Header: 'DNS',
          accessor: 'dns'
        },
        {
          Header: 'REPO',
          accessor: 'repo'
        },
        {
          Header: 'APP',
          accessor: 'app'
        },
        {
          Header: 'PROXY',
          accessor: 'proxy'
        },
        {
          Header: 'PRES',
          accessor: 'pres'
        },
        {
          Header: 'DB',
          accessor: 'db'
        },
        {
          Header: 'NTPD',
          accessor: 'ntpd'
        },
        {
          Header: 'TURN',
          accessor: 'turn'
        },
        {
          Header: 'BROKER',
          accessor: 'broker'
        },
      ]}
    />
  );
};

export default Table;
