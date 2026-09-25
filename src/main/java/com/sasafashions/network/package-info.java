/**
 * Provides TCP networking functionality for the
 * Sasa Fashions Management System.
 *
 * <p>The networking package allows approved client programs
 * to communicate with the Sasa Fashions application server
 * without receiving direct access to the MySQL database.</p>
 *
 * <p>The principal classes are:</p>
 *
 * <ul>
 *     <li>
 *         {@link com.sasafashions.network.SasaNetworkServer}
 *         - accepts TCP client connections
 *     </li>
 *
 *     <li>
 *         {@link com.sasafashions.network.ClientHandler}
 *         - processes commands from one connected client
 *     </li>
 *
 *     <li>
 *         {@link com.sasafashions.network.NetworkDataService}
 *         - provides controlled access to Sasa Fashions
 *         business data
 *     </li>
 *
 *     <li>
 *         {@link com.sasafashions.network.NetworkClient}
 *         - sends commands to the server
 *     </li>
 * </ul>
 *
 * <p>The package demonstrates Java sockets, TCP communication,
 * threads, interfaces, exception handling, object collaboration,
 * JDBC integration and separation of concerns.</p>
 *
 * @author SASA Group
 * @version 1.2
 */
package com.sasafashions.network;