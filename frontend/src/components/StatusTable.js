import React from 'react';

export const Table = ({ data }) => {
  return (
      <table>
       <tbody>
          {data.map((row, index) => (
            <tr key={index}>
                {row.map((element, index) => (
                    <td key={index} style={{border : '1px solid black'}}>{element}</td>
                ))}
            </tr>
            ))}
        </tbody>
        </table>
    );
};
