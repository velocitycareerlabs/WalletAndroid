/**
 * Created by Michael Avoyan on 12/05/2021.
 *
 * Copyright 2022 Velocity Career Labs inc.
 * SPDX-License-Identifier: Apache-2.0
 */

package io.velocitycareerlabs.infrastructure.resources.valid

class CredentialMocks {
    companion object {
        const val JwtCredEmailWithoutSubjectJwt =
            "eyJ0eXAiOiJKV1QiLCJqd2siOnsiY3J2Ijoic2VjcDI1NmsxIiwieCI6IkZOV0NZcmVDZlkyYk1VbDRIbW9oQWxSdnFJb0hPOFlnT1hpOE5IR2V4RjgiLCJ5IjoiNzBTRTU1RElMd3lWSko0YWRKUXVYVE1fdlJVY3RweWxQMnJsS3dLYnZxTSIsImt0eSI6IkVDIiwia2lkIjoiSzRtYTVBRVhYT0lMSmc0MjhNLXdfbmRwN2ltRExoaVFxdTBCUDAzUnRQWSIsImFsZyI6IkVTMjU2SyIsInVzZSI6InNpZyJ9LCJhbGciOiJFUzI1NksifQ.eyJ2YyI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSJdLCJ0eXBlIjpbIkVtYWlsVjEuMCIsIlZlcmlmaWFibGVDcmVkZW50aWFsIl0sImNyZWRlbnRpYWxTdWJqZWN0Ijp7ImVtYWlsIjoiYWRhbS5zbWl0aEBleGFtcGxlLmNvbSJ9fSwiaXNzIjoiZGlkOnZlbG9jaXR5OjB4YmE3ZDg3ZjlkNWU0NzNkN2QzYTgyZDE1MjkyM2FkYjUzZGU4ZmMwZSIsImp0aSI6ImRpZDp2ZWxvY2l0eToweGE1M2RlNmJkYTM4YjVlNjVmZWVmNTk3ODZiMTRlNTRhNmY5YzRhNzMiLCJpYXQiOjE2MzQ1MTA4OTMsIm5iZiI6MTYzNDUxMDg5M30.SJwSm86k1rQc5x-JS3cup_8WqA6p06EZo1HP6smNGep8XWSxfcnUHGBYKwGDAOILVoBtQQLCK488BaQ8NSNsbw"

        const val JwtCredPhoneWithoutSubjectJwt =
            "eyJ0eXAiOiJKV1QiLCJqd2siOnsiY3J2Ijoic2VjcDI1NmsxIiwieCI6IjFtNi1ZSWtHZTA3MmxYcUNqd1RCTExhMnN6bTZ1cGtMTTNjZnY4eVF6ZEEiLCJ5IjoiNDVBWkJlU2xVOUlSSUR5MHA5RF9kaFR4MkZ4dGQtMlBGdkVma3dsZnRGZyIsImt0eSI6IkVDIiwia2lkIjoiZnV0c2VQQUNRdFVJWnRNVlRMR1RYZzFXMGlUZG1odXJBVHZpcmxES3BwZyIsImFsZyI6IkVTMjU2SyIsInVzZSI6InNpZyJ9LCJhbGciOiJFUzI1NksifQ.eyJ2YyI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSJdLCJ0eXBlIjpbIlBob25lVjEuMCIsIlZlcmlmaWFibGVDcmVkZW50aWFsIl0sImNyZWRlbnRpYWxTdWJqZWN0Ijp7InBob25lIjoiKzE1NTU2MTkyMTkxIn19LCJpc3MiOiJkaWQ6dmVsb2NpdHk6MHhiYTdkODdmOWQ1ZTQ3M2Q3ZDNhODJkMTUyOTIzYWRiNTNkZThmYzBlIiwianRpIjoiZGlkOnZlbG9jaXR5OjB4OGNlMzk4Y2VmNGY3ZWQ4ZWI1MGEyOGQyNWM4NjNlZWY5NjhiYjBlZSIsImlhdCI6MTYzNDUxMDg5NCwibmJmIjoxNjM0NTEwODk0fQ.g3YivH_Quiw95TywvTmiv2CBWsp5JrrCcbpOcTtYpMAQNQJD7Q3kmMYTBs1Zg3tKFRPSJ_XozFIXug5nsn2SGg"

        const val JwtCredentialEducationDegreeRegistrationFromRegularIssuer =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NksiLCJraWQiOiJkaWQ6dmVsb2NpdHk6djI6MHhmMGY5MmM1ZWIxNmQ4ZTExMjUzMTk4N2Q3YjVhYmIxZTVjYjVlYmFlOjIxMjk3MTQ1OTk5MDYzNDo0MTU0I2tleS0xIn0.eyJ2YyI6eyJ0eXBlIjpbIkVkdWNhdGlvbkRlZ3JlZVJlZ2lzdHJhdGlvblYxLjEiLCJWZXJpZmlhYmxlQ3JlZGVudGlhbCJdLCJpZCI6ImRpZDp2ZWxvY2l0eTp2MjoweGYwZjkyYzVlYjE2ZDhlMTEyNTMxOTg3ZDdiNWFiYjFlNWNiNWViYWU6MjEyOTcxNDU5OTkwNjM0OjQxNTQiLCJjcmVkZW50aWFsU3RhdHVzIjp7InR5cGUiOiJWZWxvY2l0eVJldm9jYXRpb25MaXN0SmFuMjAyMSIsImlkIjoiZXRoZXJldW06MHhEODkwRjJENjBCNDI5ZjllMjU3RkMwQmM1OEVmMjIzNzc3NkREOTFCL2dldFJldm9rZWRTdGF0dXM_YWRkcmVzcz0weEYwZjkyYzVlYjE2ZDhFMTEyNTMxOTg3RDdCNWFiQjFlNWNiNWVCQUUmbGlzdElkPTExNjIwNDIzMDczNjk0MSZpbmRleD0zOTE1Iiwic3RhdHVzTGlzdEluZGV4IjozOTE1LCJzdGF0dXNMaXN0Q3JlZGVudGlhbCI6ImV0aGVyZXVtOjB4RDg5MEYyRDYwQjQyOWY5ZTI1N0ZDMEJjNThFZjIyMzc3NzZERDkxQi9nZXRSZXZva2VkU3RhdHVzP2FkZHJlc3M9MHhGMGY5MmM1ZWIxNmQ4RTExMjUzMTk4N0Q3QjVhYkIxZTVjYjVlQkFFJmxpc3RJZD0xMTYyMDQyMzA3MzY5NDEifSwibGlua0NvZGVDb21taXRtZW50Ijp7InR5cGUiOiJWZWxvY2l0eUNyZWRlbnRpYWxMaW5rQ29kZUNvbW1pdG1lbnQyMDIyIiwidmFsdWUiOiJFaUE2TEMycUZVVE9uMTV6Q09CVGRqdTdvOXFESVhsaE9qK2p2dDVoSnFBVjBnPT0ifSwiaXNzdWVyIjp7ImlkIjoiZGlkOmlvbjpFaUJNc3cyN0lLUllJZHdVT2ZEZUJkMExuV1ZlRzJmUHh4Smk5TDFmdmpNMjBnIn0sImNvbnRlbnRIYXNoIjp7InR5cGUiOiJWZWxvY2l0eUNvbnRlbnRIYXNoMjAyMCIsInZhbHVlIjoiYmU5ODY0NTUyNDQ4MWVkYzVhYjU0NzVlYmE0NmQ0ZjFiNDM1NzhkZjUxMmFiYWJmZTQ1NDQ1MTA0OTg4N2JiZSJ9LCJjcmVkZW50aWFsU2NoZW1hIjp7ImlkIjoiaHR0cHM6Ly9kZXZyZWdpc3RyYXIudmVsb2NpdHluZXR3b3JrLmZvdW5kYXRpb24vc2NoZW1hcy9lZHVjYXRpb24tZGVncmVlLXJlZ2lzdHJhdGlvbi12MS4xLnNjaGVtYS5qc29uIiwidHlwZSI6Ikpzb25TY2hlbWFWYWxpZGF0b3IyMDE4In0sInZuZlByb3RvY29sVmVyc2lvbiI6MSwiY3JlZGVudGlhbFN1YmplY3QiOnsiaW5zdGl0dXRpb24iOnsibmFtZSI6IlJlZ3VsYXIgSXNzdWVyIiwiaWRlbnRpZmllciI6ImRpZDppb246RWlCTXN3MjdJS1JZSWR3VU9mRGVCZDBMbldWZUcyZlB4eEppOUwxZnZqTTIwZyIsInBsYWNlIjp7ImFkZHJlc3NDb3VudHJ5IjoiVVMiLCJ0eXBlIjoiUGxhY2UifSwidHlwZSI6Ik9yZ2FuaXphdGlvbiJ9LCJzY2hvb2wiOnsibmFtZSI6IlNjaG9vbCBvZiBudXJzaW5nIiwicGxhY2UiOnsiYWRkcmVzc0NvdW50cnkiOiJVUyIsInR5cGUiOiJQbGFjZSJ9LCJ0eXBlIjoiT3JnYW5pemF0aW9uIn0sInByb2dyYW1OYW1lIjoiUk4gdG8gQlNOIiwicHJvZ3JhbVR5cGUiOiIxIHllYXIgZnVsbCB0aW1lIHByb2dyYW0iLCJwcm9ncmFtTW9kZSI6Ik9ubGluZSIsImRlZ3JlZU5hbWUiOiJSZWd1bGFyIElzc3VpbmcgMiIsImRlc2NyaXB0aW9uIjoiU3RhcmZpZWxkIENvbGxlZ2UiLCJyZWdpc3RyYXRpb25EYXRlIjoiMjAxMS0wNS0xNSIsInN0YXJ0RGF0ZSI6IjIwMTEtMTAtMDIiLCJyZWNpcGllbnQiOnsiZ2l2ZW5OYW1lIjoiT2xpdmlhIiwiZmFtaWx5TmFtZSI6IkhhZmV6IiwidHlwZSI6IlBlcnNvbk5hbWUifSwidHlwZSI6IkVkdWNhdGlvbkRlZ3JlZSIsIkBjb250ZXh0IjpbImh0dHBzOi8vdmVsb2NpdHluZXR3b3JrLmZvdW5kYXRpb24vY29udGV4dHMvZWR1Y2F0aW9uLWRlZ3JlZSIsImh0dHBzOi8vZGV2bGliLnZlbG9jaXR5bmV0d29yay5mb3VuZGF0aW9uL2NvbnRleHRzL2xheWVyMS12MS4xLmpzb25sZC5qc29uIl19fSwibmJmIjoxNjg5MTk0ODg5LCJqdGkiOiJkaWQ6dmVsb2NpdHk6djI6MHhmMGY5MmM1ZWIxNmQ4ZTExMjUzMTk4N2Q3YjVhYmIxZTVjYjVlYmFlOjIxMjk3MTQ1OTk5MDYzNDo0MTU0IiwiaXNzIjoiZGlkOmlvbjpFaUJNc3cyN0lLUllJZHdVT2ZEZUJkMExuV1ZlRzJmUHh4Smk5TDFmdmpNMjBnIiwiaWF0IjoxNjg5MTk0ODg5fQ.esqjqTQ0tOy-yMuja4ELKA1X8tKPbI0zqRXtq4Nmw2hCTD39K2GzOGfsER4CO2se4x5G1zkd_PwVA7U5-AEiZQ"

        const val JwtCredentialEmploymentPastFromRegularIssuer =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NksiLCJraWQiOiJkaWQ6dmVsb2NpdHk6djI6MHhmMGY5MmM1ZWIxNmQ4ZTExMjUzMTk4N2Q3YjVhYmIxZTVjYjVlYmFlOjIxMjk3MTQ1OTk5MDYzNDo1NjQ5I2tleS0xIn0.eyJ2YyI6eyJ0eXBlIjpbIkVtcGxveW1lbnRQYXN0VjEuMSIsIlZlcmlmaWFibGVDcmVkZW50aWFsIl0sImlkIjoiZGlkOnZlbG9jaXR5OnYyOjB4ZjBmOTJjNWViMTZkOGUxMTI1MzE5ODdkN2I1YWJiMWU1Y2I1ZWJhZToyMTI5NzE0NTk5OTA2MzQ6NTY0OSIsImNyZWRlbnRpYWxTdGF0dXMiOnsidHlwZSI6IlZlbG9jaXR5UmV2b2NhdGlvbkxpc3RKYW4yMDIxIiwiaWQiOiJldGhlcmV1bToweEQ4OTBGMkQ2MEI0MjlmOWUyNTdGQzBCYzU4RWYyMjM3Nzc2REQ5MUIvZ2V0UmV2b2tlZFN0YXR1cz9hZGRyZXNzPTB4RjBmOTJjNWViMTZkOEUxMTI1MzE5ODdEN0I1YWJCMWU1Y2I1ZUJBRSZsaXN0SWQ9MTE2MjA0MjMwNzM2OTQxJmluZGV4PTExOTUiLCJzdGF0dXNMaXN0SW5kZXgiOjExOTUsInN0YXR1c0xpc3RDcmVkZW50aWFsIjoiZXRoZXJldW06MHhEODkwRjJENjBCNDI5ZjllMjU3RkMwQmM1OEVmMjIzNzc3NkREOTFCL2dldFJldm9rZWRTdGF0dXM_YWRkcmVzcz0weEYwZjkyYzVlYjE2ZDhFMTEyNTMxOTg3RDdCNWFiQjFlNWNiNWVCQUUmbGlzdElkPTExNjIwNDIzMDczNjk0MSJ9LCJsaW5rQ29kZUNvbW1pdG1lbnQiOnsidHlwZSI6IlZlbG9jaXR5Q3JlZGVudGlhbExpbmtDb2RlQ29tbWl0bWVudDIwMjIiLCJ2YWx1ZSI6IkVpQ3JuR2diVjcyb2h1RXNSbmlUOE92Y0V3dzZiRkVQRkg5N1VXaC84Tk1scEE9PSJ9LCJpc3N1ZXIiOnsiaWQiOiJkaWQ6aW9uOkVpQk1zdzI3SUtSWUlkd1VPZkRlQmQwTG5XVmVHMmZQeHhKaTlMMWZ2ak0yMGcifSwiY29udGVudEhhc2giOnsidHlwZSI6IlZlbG9jaXR5Q29udGVudEhhc2gyMDIwIiwidmFsdWUiOiI4YTNlNTg2NTg0MGU4Y2MyY2MxN2YwYWU2YjY4MzA3YmIzZDU3YzE1ODA1ZDQ5MTM1YWFhMjgwYmJlMGE4MGI0In0sImNyZWRlbnRpYWxTY2hlbWEiOnsiaWQiOiJodHRwczovL2RldnJlZ2lzdHJhci52ZWxvY2l0eW5ldHdvcmsuZm91bmRhdGlvbi9zY2hlbWFzL2VtcGxveW1lbnQtcGFzdC12MS4xLnNjaGVtYS5qc29uIiwidHlwZSI6Ikpzb25TY2hlbWFWYWxpZGF0b3IyMDE4In0sInZuZlByb3RvY29sVmVyc2lvbiI6MSwiY3JlZGVudGlhbFN1YmplY3QiOnsibGVnYWxFbXBsb3llciI6eyJuYW1lIjoiUVIgT2ZmZXIgMSIsImlkZW50aWZpZXIiOiJkaWQ6aW9uOkVpQk1zdzI3SUtSWUlkd1VPZkRlQmQwTG5XVmVHMmZQeHhKaTlMMWZ2ak0yMGciLCJwbGFjZSI6eyJhZGRyZXNzQ291bnRyeSI6IlVTIiwidHlwZSI6IlBsYWNlIn0sInR5cGUiOiJPcmdhbml6YXRpb24ifSwicm9sZSI6IlJlZ3VsYXIgaXNzdWluZyAxIiwiZGVzY3JpcHRpb24iOiJCYWNrZW5kIGRldmVsb3BtZW50IHByb2plY3QgbWFuYWdlbWVudCIsImVtcGxveW1lbnRUeXBlIjpbInBhcnQtdGltZSIsInBlcm1hbmVudCJdLCJwbGFjZSI6eyJuYW1lIjoiTWVkaWEgTGFiIiwiYWRkcmVzc0NvdW50cnkiOiJVUyIsInR5cGUiOiJQbGFjZSJ9LCJzdGFydERhdGUiOiIyMDEzLTEwLTAxIiwiZW5kRGF0ZSI6IjIwMTctMDEtMDEiLCJyZWNpcGllbnQiOnsiZ2l2ZW5OYW1lIjoiT2xpdmlhIiwiZmFtaWx5TmFtZSI6IkhhZmV6IiwidHlwZSI6IlBlcnNvbk5hbWUifSwidHlwZSI6IkVtcGxveW1lbnQiLCJAY29udGV4dCI6WyJodHRwczovL3ZlbG9jaXR5bmV0d29yay5mb3VuZGF0aW9uL2NvbnRleHRzL2VtcGxveW1lbnQiLCJodHRwczovL2RldmxpYi52ZWxvY2l0eW5ldHdvcmsuZm91bmRhdGlvbi9jb250ZXh0cy9sYXllcjEtdjEuMS5qc29ubGQuanNvbiJdfX0sIm5iZiI6MTY4OTE5NDg4OSwianRpIjoiZGlkOnZlbG9jaXR5OnYyOjB4ZjBmOTJjNWViMTZkOGUxMTI1MzE5ODdkN2I1YWJiMWU1Y2I1ZWJhZToyMTI5NzE0NTk5OTA2MzQ6NTY0OSIsImlzcyI6ImRpZDppb246RWlCTXN3MjdJS1JZSWR3VU9mRGVCZDBMbldWZUcyZlB4eEppOUwxZnZqTTIwZyIsImlhdCI6MTY4OTE5NDg4OX0.DmxfN5ugs6Wrk5CMeJ8rGx-jbP4bElxYBwbU0ob1Jjv0Q8JajSzSJ67Hbw-ha7Yb9Irfuc6VGrZM9r5KRXOuww"

        const val JwtCredentialEducationDegreeRegistrationFromNotaryIssuer =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NksiLCJraWQiOiJkaWQ6dmVsb2NpdHk6djI6MHhlZTY3NDE3ZGNkOWI2ZDIzOWRjOTJjMzU1ZTZhZTE2MWEzZDE1NGZkOjI3NTc0NDEyMzg1MTU5OjM0NjAja2V5LTEifQ.eyJ2YyI6eyJ0eXBlIjpbIkVkdWNhdGlvbkRlZ3JlZVJlZ2lzdHJhdGlvblYxLjEiLCJWZXJpZmlhYmxlQ3JlZGVudGlhbCJdLCJpZCI6ImRpZDp2ZWxvY2l0eTp2MjoweGVlNjc0MTdkY2Q5YjZkMjM5ZGM5MmMzNTVlNmFlMTYxYTNkMTU0ZmQ6Mjc1NzQ0MTIzODUxNTk6MzQ2MCIsImNyZWRlbnRpYWxTdGF0dXMiOnsidHlwZSI6IlZlbG9jaXR5UmV2b2NhdGlvbkxpc3RKYW4yMDIxIiwiaWQiOiJldGhlcmV1bToweEQ4OTBGMkQ2MEI0MjlmOWUyNTdGQzBCYzU4RWYyMjM3Nzc2REQ5MUIvZ2V0UmV2b2tlZFN0YXR1cz9hZGRyZXNzPTB4ZWU2NzQxN2RjZDliNkQyMzlkYzkyYzM1NWU2YWUxNjFBM0QxNTRGRCZsaXN0SWQ9MTQxNDg0MTMzMTk2NTYyJmluZGV4PTE0NjciLCJzdGF0dXNMaXN0SW5kZXgiOjE0NjcsInN0YXR1c0xpc3RDcmVkZW50aWFsIjoiZXRoZXJldW06MHhEODkwRjJENjBCNDI5ZjllMjU3RkMwQmM1OEVmMjIzNzc3NkREOTFCL2dldFJldm9rZWRTdGF0dXM_YWRkcmVzcz0weGVlNjc0MTdkY2Q5YjZEMjM5ZGM5MmMzNTVlNmFlMTYxQTNEMTU0RkQmbGlzdElkPTE0MTQ4NDEzMzE5NjU2MiJ9LCJsaW5rQ29kZUNvbW1pdG1lbnQiOnsidHlwZSI6IlZlbG9jaXR5Q3JlZGVudGlhbExpbmtDb2RlQ29tbWl0bWVudDIwMjIiLCJ2YWx1ZSI6IkVpQTdENHZVdGhubE5xTXpvSnNMckZUNVpYb3k5M1Arb1pBOEovVjlWNDhhZWc9PSJ9LCJpc3N1ZXIiOnsiaWQiOiJkaWQ6aW9uOkVpQ1ZEVERRV25YTTcxekJvazdTTWozZ0I5SkozdXRXTkJlY2pXcEQycDdPU0EifSwiY29udGVudEhhc2giOnsidHlwZSI6IlZlbG9jaXR5Q29udGVudEhhc2gyMDIwIiwidmFsdWUiOiI5OTVhODNmNWIxODViZDg5OTc5NzJmNzUzYmFmNTZkNDEzZGI5NThkNGVmN2M0YmU4NzE4NGNmYzRhYmViODZmIn0sImNyZWRlbnRpYWxTY2hlbWEiOnsiaWQiOiJodHRwczovL2RldnJlZ2lzdHJhci52ZWxvY2l0eW5ldHdvcmsuZm91bmRhdGlvbi9zY2hlbWFzL2VkdWNhdGlvbi1kZWdyZWUtcmVnaXN0cmF0aW9uLXYxLjEuc2NoZW1hLmpzb24iLCJ0eXBlIjoiSnNvblNjaGVtYVZhbGlkYXRvcjIwMTgifSwidm5mUHJvdG9jb2xWZXJzaW9uIjoxLCJjcmVkZW50aWFsU3ViamVjdCI6eyJpbnN0aXR1dGlvbiI6eyJuYW1lIjoiTm90YXJ5IElzc3VlciIsImlkZW50aWZpZXIiOiJkaWQ6aW9uOkVpQk1zdzI3SUtSWUlkd1VPZkRlQmQwTG5XVmVHMmZQeHhKaTlMMWZ2ak0yMGciLCJwbGFjZSI6eyJhZGRyZXNzQ291bnRyeSI6IlVTIiwidHlwZSI6IlBsYWNlIn0sInR5cGUiOiJPcmdhbml6YXRpb24ifSwic2Nob29sIjp7Im5hbWUiOiJTY2hvb2wgb2YgbnVyc2luZyIsInBsYWNlIjp7ImFkZHJlc3NDb3VudHJ5IjoiVVMiLCJ0eXBlIjoiUGxhY2UifSwidHlwZSI6Ik9yZ2FuaXphdGlvbiJ9LCJwcm9ncmFtTmFtZSI6IlJOIHRvIEJTTiIsInByb2dyYW1UeXBlIjoiMSB5ZWFyIGZ1bGwgdGltZSBwcm9ncmFtIiwicHJvZ3JhbU1vZGUiOiJPbmxpbmUiLCJkZWdyZWVOYW1lIjoiTm90YXJ5IElzc3VpbmcgMiIsImRlc2NyaXB0aW9uIjoiU3RhcmZpZWxkIENvbGxlZ2UiLCJyZWdpc3RyYXRpb25EYXRlIjoiMjAxMS0wNS0xNSIsInN0YXJ0RGF0ZSI6IjIwMTEtMTAtMDIiLCJyZWNpcGllbnQiOnsiZ2l2ZW5OYW1lIjoiT2xpdmlhIiwiZmFtaWx5TmFtZSI6IkhhZmV6IiwidHlwZSI6IlBlcnNvbk5hbWUifSwidHlwZSI6IkVkdWNhdGlvbkRlZ3JlZSIsIkBjb250ZXh0IjpbImh0dHBzOi8vdmVsb2NpdHluZXR3b3JrLmZvdW5kYXRpb24vY29udGV4dHMvZWR1Y2F0aW9uLWRlZ3JlZSIsImh0dHBzOi8vZGV2bGliLnZlbG9jaXR5bmV0d29yay5mb3VuZGF0aW9uL2NvbnRleHRzL2xheWVyMS12MS4xLmpzb25sZC5qc29uIl19fSwibmJmIjoxNjg5MTk1NzY1LCJqdGkiOiJkaWQ6dmVsb2NpdHk6djI6MHhlZTY3NDE3ZGNkOWI2ZDIzOWRjOTJjMzU1ZTZhZTE2MWEzZDE1NGZkOjI3NTc0NDEyMzg1MTU5OjM0NjAiLCJpc3MiOiJkaWQ6aW9uOkVpQ1ZEVERRV25YTTcxekJvazdTTWozZ0I5SkozdXRXTkJlY2pXcEQycDdPU0EiLCJpYXQiOjE2ODkxOTU3NjV9.OB7ajCYTjZ02tUnl1X0fOKX0vtiDbkuRkO3MQVlvGDNXHoGzIaeSDwi-m4s4Rs-lc7ypT3a7eGXO0fy38d2VNw"

        const val JwtCredentialEmploymentPastFromNotaryIssuer =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NksiLCJraWQiOiJkaWQ6dmVsb2NpdHk6djI6MHhlZTY3NDE3ZGNkOWI2ZDIzOWRjOTJjMzU1ZTZhZTE2MWEzZDE1NGZkOjI3NTc0NDEyMzg1MTU5OjQ1MzIja2V5LTEifQ.eyJ2YyI6eyJ0eXBlIjpbIkVtcGxveW1lbnRQYXN0VjEuMSIsIlZlcmlmaWFibGVDcmVkZW50aWFsIl0sImlkIjoiZGlkOnZlbG9jaXR5OnYyOjB4ZWU2NzQxN2RjZDliNmQyMzlkYzkyYzM1NWU2YWUxNjFhM2QxNTRmZDoyNzU3NDQxMjM4NTE1OTo0NTMyIiwiY3JlZGVudGlhbFN0YXR1cyI6eyJ0eXBlIjoiVmVsb2NpdHlSZXZvY2F0aW9uTGlzdEphbjIwMjEiLCJpZCI6ImV0aGVyZXVtOjB4RDg5MEYyRDYwQjQyOWY5ZTI1N0ZDMEJjNThFZjIyMzc3NzZERDkxQi9nZXRSZXZva2VkU3RhdHVzP2FkZHJlc3M9MHhlZTY3NDE3ZGNkOWI2RDIzOWRjOTJjMzU1ZTZhZTE2MUEzRDE1NEZEJmxpc3RJZD0xNDE0ODQxMzMxOTY1NjImaW5kZXg9NTMiLCJzdGF0dXNMaXN0SW5kZXgiOjUzLCJzdGF0dXNMaXN0Q3JlZGVudGlhbCI6ImV0aGVyZXVtOjB4RDg5MEYyRDYwQjQyOWY5ZTI1N0ZDMEJjNThFZjIyMzc3NzZERDkxQi9nZXRSZXZva2VkU3RhdHVzP2FkZHJlc3M9MHhlZTY3NDE3ZGNkOWI2RDIzOWRjOTJjMzU1ZTZhZTE2MUEzRDE1NEZEJmxpc3RJZD0xNDE0ODQxMzMxOTY1NjIifSwibGlua0NvZGVDb21taXRtZW50Ijp7InR5cGUiOiJWZWxvY2l0eUNyZWRlbnRpYWxMaW5rQ29kZUNvbW1pdG1lbnQyMDIyIiwidmFsdWUiOiJFaUR1VS93czJWaWl3Tll2bW80RUtCN1k5UGVYY01DdW95MmhyMHpkdFJSbGNnPT0ifSwiaXNzdWVyIjp7ImlkIjoiZGlkOmlvbjpFaUNWRFREUVduWE03MXpCb2s3U01qM2dCOUpKM3V0V05CZWNqV3BEMnA3T1NBIn0sImNvbnRlbnRIYXNoIjp7InR5cGUiOiJWZWxvY2l0eUNvbnRlbnRIYXNoMjAyMCIsInZhbHVlIjoiMjBjZWE1N2QwMGNiYjk3MjQwNGEyOGNkYzAzYjY5ZDdiNGUwOWE2NWUwYjYxZWNhNTE3ZmIyYzhiNzEyYjZlYiJ9LCJjcmVkZW50aWFsU2NoZW1hIjp7ImlkIjoiaHR0cHM6Ly9kZXZyZWdpc3RyYXIudmVsb2NpdHluZXR3b3JrLmZvdW5kYXRpb24vc2NoZW1hcy9lbXBsb3ltZW50LXBhc3QtdjEuMS5zY2hlbWEuanNvbiIsInR5cGUiOiJKc29uU2NoZW1hVmFsaWRhdG9yMjAxOCJ9LCJ2bmZQcm90b2NvbFZlcnNpb24iOjEsImNyZWRlbnRpYWxTdWJqZWN0Ijp7ImxlZ2FsRW1wbG95ZXIiOnsibmFtZSI6Ik5vdGFyeSBJc3N1ZXIgMSIsImlkZW50aWZpZXIiOiJkaWQ6aW9uOkVpQ1ZEVERRV25YTTcxekJvazdTTWozZ0I5SkozdXRXTkJlY2pXcEQycDdPU0EiLCJwbGFjZSI6eyJhZGRyZXNzQ291bnRyeSI6IlVTIiwidHlwZSI6IlBsYWNlIn0sInR5cGUiOiJPcmdhbml6YXRpb24ifSwicm9sZSI6Ik5vdGFyeSBpc3N1aW5nIDEiLCJkZXNjcmlwdGlvbiI6IkJhY2tlbmQgZGV2ZWxvcG1lbnQgcHJvamVjdCBtYW5hZ2VtZW50IiwiZW1wbG95bWVudFR5cGUiOlsicGFydC10aW1lIiwicGVybWFuZW50Il0sInBsYWNlIjp7Im5hbWUiOiJNZWRpYSBMYWIiLCJhZGRyZXNzQ291bnRyeSI6IlVTIiwidHlwZSI6IlBsYWNlIn0sInN0YXJ0RGF0ZSI6IjIwMTMtMTAtMDEiLCJlbmREYXRlIjoiMjAxNy0wMS0wMSIsInJlY2lwaWVudCI6eyJnaXZlbk5hbWUiOiJPbGl2aWEiLCJmYW1pbHlOYW1lIjoiSGFmZXoiLCJ0eXBlIjoiUGVyc29uTmFtZSJ9LCJ0eXBlIjoiRW1wbG95bWVudCIsIkBjb250ZXh0IjpbImh0dHBzOi8vdmVsb2NpdHluZXR3b3JrLmZvdW5kYXRpb24vY29udGV4dHMvZW1wbG95bWVudCIsImh0dHBzOi8vZGV2bGliLnZlbG9jaXR5bmV0d29yay5mb3VuZGF0aW9uL2NvbnRleHRzL2xheWVyMS12MS4xLmpzb25sZC5qc29uIl19fSwibmJmIjoxNjg5MTk1NzY1LCJqdGkiOiJkaWQ6dmVsb2NpdHk6djI6MHhlZTY3NDE3ZGNkOWI2ZDIzOWRjOTJjMzU1ZTZhZTE2MWEzZDE1NGZkOjI3NTc0NDEyMzg1MTU5OjQ1MzIiLCJpc3MiOiJkaWQ6aW9uOkVpQ1ZEVERRV25YTTcxekJvazdTTWozZ0I5SkozdXRXTkJlY2pXcEQycDdPU0EiLCJpYXQiOjE2ODkxOTU3NjV9.lVpSM1c9Q0A78S7eE45cpvgbkjeC2qhcjaqyon_qxjbvU1obG5x4BmywCceigKdkSe4hw1myMr5ay2C7WglkjA"

        const val JwtCredentialEmailFromIdentityIssuer =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NksiLCJraWQiOiJkaWQ6dmVsb2NpdHk6djI6MHgxZTgzMzA0NGVmOTU5NjJlMTMwYjRiZWY5MTdkNTc4NDE1YmMxZGE1OjE1NjY1Njk2ODU1NjI4NzozOTQxI2tleS0xIn0.eyJ2YyI6eyJ0eXBlIjpbIkVtYWlsVjEuMCIsIlZlcmlmaWFibGVDcmVkZW50aWFsIl0sImlkIjoiZGlkOnZlbG9jaXR5OnYyOjB4MWU4MzMwNDRlZjk1OTYyZTEzMGI0YmVmOTE3ZDU3ODQxNWJjMWRhNToxNTY2NTY5Njg1NTYyODc6Mzk0MSIsImNyZWRlbnRpYWxTdGF0dXMiOnsidHlwZSI6IlZlbG9jaXR5UmV2b2NhdGlvbkxpc3RKYW4yMDIxIiwiaWQiOiJldGhlcmV1bToweEQ4OTBGMkQ2MEI0MjlmOWUyNTdGQzBCYzU4RWYyMjM3Nzc2REQ5MUIvZ2V0UmV2b2tlZFN0YXR1cz9hZGRyZXNzPTB4MWU4MzMwNDRlRjk1OTYyRTEzMEI0YkVmOTE3ZDU3ODQxNUJjMURBNSZsaXN0SWQ9MjI5MTAxMTgzOTQyMTA1JmluZGV4PTc4NjMiLCJzdGF0dXNMaXN0SW5kZXgiOjc4NjMsInN0YXR1c0xpc3RDcmVkZW50aWFsIjoiZXRoZXJldW06MHhEODkwRjJENjBCNDI5ZjllMjU3RkMwQmM1OEVmMjIzNzc3NkREOTFCL2dldFJldm9rZWRTdGF0dXM_YWRkcmVzcz0weDFlODMzMDQ0ZUY5NTk2MkUxMzBCNGJFZjkxN2Q1Nzg0MTVCYzFEQTUmbGlzdElkPTIyOTEwMTE4Mzk0MjEwNSJ9LCJsaW5rQ29kZUNvbW1pdG1lbnQiOnsidHlwZSI6IlZlbG9jaXR5Q3JlZGVudGlhbExpbmtDb2RlQ29tbWl0bWVudDIwMjIiLCJ2YWx1ZSI6IkVpQTdydFdQZXBvNllHMEw4SFBVVy9ObE9LS0MvTHdYS1Z2RzVsMTQwZDlrUlE9PSJ9LCJpc3N1ZXIiOnsiaWQiOiJkaWQ6aW9uOkVpRHhUcTVaSExqeThLLVU5dk44WjNyOGtGdmd1WlpITU00OElXZW5EZTgxYVEifSwiY29udGVudEhhc2giOnsidHlwZSI6IlZlbG9jaXR5Q29udGVudEhhc2gyMDIwIiwidmFsdWUiOiJhY2UxZjI0ODQ4MTNhZTJlM2QxMWNhMjdmZjIyN2Q5NDgwMjIwNjFhZDVmMGZmNjUzNzY5Mzc5ZTgzMzgyNTNiIn0sImNyZWRlbnRpYWxTY2hlbWEiOnsiaWQiOiJodHRwczovL2RldnJlZ2lzdHJhci52ZWxvY2l0eW5ldHdvcmsuZm91bmRhdGlvbi9zY2hlbWFzL2VtYWlsLXYxLjAuc2NoZW1hLmpzb24iLCJ0eXBlIjoiSnNvblNjaGVtYVZhbGlkYXRvcjIwMTgifSwiY3JlZGVudGlhbFN1YmplY3QiOnsiZW1haWwiOiJhZGFtLnNtaXRoQGV4YW1wbGUuY29tIiwidHlwZSI6IkVtYWlsIiwiQGNvbnRleHQiOlsiaHR0cHM6Ly9kZXZsaWIudmVsb2NpdHluZXR3b3JrLmZvdW5kYXRpb24vY29udGV4dHMvbGF5ZXIxLXYxLjAuanNvbmxkLmpzb24iXX19LCJuYmYiOjE2ODkxOTgwNDcsImp0aSI6ImRpZDp2ZWxvY2l0eTp2MjoweDFlODMzMDQ0ZWY5NTk2MmUxMzBiNGJlZjkxN2Q1Nzg0MTViYzFkYTU6MTU2NjU2OTY4NTU2Mjg3OjM5NDEiLCJpc3MiOiJkaWQ6aW9uOkVpRHhUcTVaSExqeThLLVU5dk44WjNyOGtGdmd1WlpITU00OElXZW5EZTgxYVEiLCJpYXQiOjE2ODkxOTgwNDd9.Net_b9MaazJoWfmvHtagYwzO43NVAi4MAbmpB4jdNE61rHpSiFuZQC9IUUJmtu0-0oV7wb9rZ7auIcYjnprS0Q"

        const val JwtCredentialPassportFromIdentityIssuer =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NksiLCJraWQiOiJkaWQ6dmVsb2NpdHk6djI6MHgxZTgzMzA0NGVmOTU5NjJlMTMwYjRiZWY5MTdkNTc4NDE1YmMxZGE1OjE1NjY1Njk2ODU1NjI4Nzo5OTk2I2tleS0xIn0.eyJ2YyI6eyJ0eXBlIjpbIlBhc3Nwb3J0VjEuMCIsIlZlcmlmaWFibGVDcmVkZW50aWFsIl0sImlkIjoiZGlkOnZlbG9jaXR5OnYyOjB4MWU4MzMwNDRlZjk1OTYyZTEzMGI0YmVmOTE3ZDU3ODQxNWJjMWRhNToxNTY2NTY5Njg1NTYyODc6OTk5NiIsImNyZWRlbnRpYWxTdGF0dXMiOnsidHlwZSI6IlZlbG9jaXR5UmV2b2NhdGlvbkxpc3RKYW4yMDIxIiwiaWQiOiJldGhlcmV1bToweEQ4OTBGMkQ2MEI0MjlmOWUyNTdGQzBCYzU4RWYyMjM3Nzc2REQ5MUIvZ2V0UmV2b2tlZFN0YXR1cz9hZGRyZXNzPTB4MWU4MzMwNDRlRjk1OTYyRTEzMEI0YkVmOTE3ZDU3ODQxNUJjMURBNSZsaXN0SWQ9MjI5MTAxMTgzOTQyMTA1JmluZGV4PTY0NDIiLCJzdGF0dXNMaXN0SW5kZXgiOjY0NDIsInN0YXR1c0xpc3RDcmVkZW50aWFsIjoiZXRoZXJldW06MHhEODkwRjJENjBCNDI5ZjllMjU3RkMwQmM1OEVmMjIzNzc3NkREOTFCL2dldFJldm9rZWRTdGF0dXM_YWRkcmVzcz0weDFlODMzMDQ0ZUY5NTk2MkUxMzBCNGJFZjkxN2Q1Nzg0MTVCYzFEQTUmbGlzdElkPTIyOTEwMTE4Mzk0MjEwNSJ9LCJsaW5rQ29kZUNvbW1pdG1lbnQiOnsidHlwZSI6IlZlbG9jaXR5Q3JlZGVudGlhbExpbmtDb2RlQ29tbWl0bWVudDIwMjIiLCJ2YWx1ZSI6IkVpQjFKWXFBUko1MFNWOVBlcUVZUkJ6TXBKMFdodURKWng1UEZLUlUxdWtxZ1E9PSJ9LCJpc3N1ZXIiOnsiaWQiOiJkaWQ6aW9uOkVpRHhUcTVaSExqeThLLVU5dk44WjNyOGtGdmd1WlpITU00OElXZW5EZTgxYVEifSwiY29udGVudEhhc2giOnsidHlwZSI6IlZlbG9jaXR5Q29udGVudEhhc2gyMDIwIiwidmFsdWUiOiIyYjkzOTQ5ZDM1ODkwZTRiNGQxOGNlZjAzODUxMjAyOGQ4MmFkYzIwYjQ4M2ZiNTRhMzAwOGYyZTM0ZjYwYTEzIn0sImNyZWRlbnRpYWxTY2hlbWEiOnsiaWQiOiJodHRwczovL2RldnJlZ2lzdHJhci52ZWxvY2l0eW5ldHdvcmsuZm91bmRhdGlvbi9zY2hlbWFzL3Bhc3Nwb3J0LXYxLjAuc2NoZW1hLmpzb24iLCJ0eXBlIjoiSnNvblNjaGVtYVZhbGlkYXRvcjIwMTgifSwiY3JlZGVudGlhbFN1YmplY3QiOnsia2luZCI6IlBhc3Nwb3J0IiwiYXV0aG9yaXR5Ijp7Im5hbWUiOiJVLlMuIERlcGFydG1lbnQgb2YgU3RhdGUiLCJwbGFjZSI6eyJhZGRyZXNzUmVnaW9uIjoiTlkiLCJhZGRyZXNzQ291bnRyeSI6IlVTIiwidHlwZSI6IlBsYWNlIn0sInR5cGUiOiJPcmdhbml6YXRpb24ifSwiaWRlbnRpZmllciI6IjM0MDAwNzIzNyIsInBlcnNvbiI6eyJnaXZlbk5hbWUiOiJPbGl2aWEiLCJmYW1pbHlOYW1lIjoiSGFmZXoiLCJiaXJ0aERhdGUiOiIxOTk2LTA2LTI3IiwidHlwZSI6IlBlcnNvbiJ9LCJ2YWxpZGl0eSI6eyJ2YWxpZEZyb20iOiIyMDIwLTA2LTI3IiwidmFsaWRVbnRpbCI6IjIwMjQtMDYtMjciLCJ0eXBlIjoiVmFsaWRpdHkifSwidHlwZSI6IlBhc3Nwb3J0IiwiQGNvbnRleHQiOlsiaHR0cHM6Ly9kZXZsaWIudmVsb2NpdHluZXR3b3JrLmZvdW5kYXRpb24vY29udGV4dHMvbGF5ZXIxLXYxLjAuanNvbmxkLmpzb24iXX19LCJuYmYiOjE2ODkxOTgwNDcsImp0aSI6ImRpZDp2ZWxvY2l0eTp2MjoweDFlODMzMDQ0ZWY5NTk2MmUxMzBiNGJlZjkxN2Q1Nzg0MTViYzFkYTU6MTU2NjU2OTY4NTU2Mjg3Ojk5OTYiLCJpc3MiOiJkaWQ6aW9uOkVpRHhUcTVaSExqeThLLVU5dk44WjNyOGtGdmd1WlpITU00OElXZW5EZTgxYVEiLCJpYXQiOjE2ODkxOTgwNDd9.-nZfFaj-C8SBb2XcgG7zzb4qKg_xtTJmLfc9SFOMmOrMAl806A25uaQsbGqD25lb0XOjbbpToQqLGcbB8R1f4Q"

        const val JwtCredentialDriversLicenseFromIdentityIssuer =
            "eyJ0eXAiOiJKV1QiLCJqd2siOnsiY3J2Ijoic2VjcDI1NmsxIiwieCI6ImxkU2gybmdRempJSnhwSktMVkpvbVpEdGQtX1JRMGpzVlgzMFB5ZmdMUEUiLCJ5IjoicEJleVJuMnFYZFNELVhTaG5WVGJCa08tb0VFRmgyV2hneU1VYTRCMHV6RSIsImt0eSI6IkVDIiwia2lkIjoiUXNiZUMzTjVHN2M0NEFQS2dKdWc2X3lhS3ZrR1lQd3B5RjRHVS1sWVpVSSIsImFsZyI6IkVTMjU2SyIsInVzZSI6InNpZyJ9LCJhbGciOiJFUzI1NksifQ.eyJ2YyI6eyJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSJdLCJ0eXBlIjpbIkRyaXZlcnNMaWNlbnNlVjEuMCIsIlZlcmlmaWFibGVDcmVkZW50aWFsIl0sImNyZWRlbnRpYWxTdWJqZWN0Ijp7ImZpcnN0TmFtZSI6eyJsb2NhbGl6ZWQiOnsiZW4iOiJBZGFtIn19LCJsYXN0TmFtZSI6eyJsb2NhbGl6ZWQiOnsiZW4iOiJTbWl0aCJ9fSwia2luZCI6IkRyaXZlcnNMaWNlbnNlIiwiYXV0aG9yaXR5Ijp7ImxvY2FsaXplZCI6eyJlbiI6IkNhbGlmb3JuaWEgRE1WIn19LCJsb2NhdGlvbiI6eyJjb3VudHJ5Q29kZSI6IlVTIiwicmVnaW9uQ29kZSI6IkNBIn0sImRvYiI6eyJkYXkiOjIwLCJtb250aCI6NiwieWVhciI6MTk2Nn0sImlkZW50aXR5TnVtYmVyIjoiMTIzMTAzMTIzMTIifX0sImlzcyI6ImRpZDp2ZWxvY2l0eToweGJhN2Q4N2Y5ZDVlNDczZDdkM2E4MmQxNTI5MjNhZGI1M2RlOGZjMGUiLCJqdGkiOiJkaWQ6dmVsb2NpdHk6MHgyOWRhZTM3ZGIzYzYwYmIyZmY5ZThiZmU4ZTM4YzkxZmUzYjlhZDIyIiwiaWF0IjoxNjM0NTEwODk2LCJuYmYiOjE2MzQ1MTA4OTZ9._nWDDYVf11-KeCKXLNaQ_t2giZB2chFIGS2IVjCHs3dXoUWNG5WC1e-dRTBGngrtOTXFUGHgqrkKHqwi9wDaCQ"

        const val JwtCredentialOpenBadgeValid =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NksiLCJraWQiOiJkaWQ6dmVsb2NpdHk6djI6MHg1NzU3MmNlYTU2ZmI0MDNkYzEzYzYwY2JiYzYzNzE1MDBlMTE0NjU2OjIzNDU3Nzk0MDIzNjM2MDo4MTEja2V5LTEifQ.eyJ2YyI6eyJ0eXBlIjpbIk9wZW5CYWRnZUNyZWRlbnRpYWwiLCJWZXJpZmlhYmxlQ3JlZGVudGlhbCJdLCJpZCI6ImRpZDp2ZWxvY2l0eTp2MjoweDU3NTcyY2VhNTZmYjQwM2RjMTNjNjBjYmJjNjM3MTUwMGUxMTQ2NTY6MjM0NTc3OTQwMjM2MzYwOjgxMSIsImNyZWRlbnRpYWxTdGF0dXMiOnsidHlwZSI6IlZlbG9jaXR5UmV2b2NhdGlvbkxpc3RKYW4yMDIxIiwiaWQiOiJldGhlcmV1bToweDE0OTljODg4NDA5RDYyYjhlQzUwMTZGNEUyYjZiYjRiZUUxRURGNDQvZ2V0UmV2b2tlZFN0YXR1cz9hZGRyZXNzPTB4NTc1NzJjRUE1NkZCNDAzRGMxM2M2MENiYkM2MzcxNTAwZTExNDY1NiZsaXN0SWQ9MTI2NjE4NzUwMjkzMDI2JmluZGV4PTY4NzUiLCJzdGF0dXNMaXN0SW5kZXgiOjY4NzUsInN0YXR1c0xpc3RDcmVkZW50aWFsIjoiZXRoZXJldW06MHgxNDk5Yzg4ODQwOUQ2MmI4ZUM1MDE2RjRFMmI2YmI0YmVFMUVERjQ0L2dldFJldm9rZWRTdGF0dXM_YWRkcmVzcz0weDU3NTcyY0VBNTZGQjQwM0RjMTNjNjBDYmJDNjM3MTUwMGUxMTQ2NTYmbGlzdElkPTEyNjYxODc1MDI5MzAyNiJ9LCJsaW5rQ29kZUNvbW1pdG1lbnQiOnsidHlwZSI6IlZlbG9jaXR5Q3JlZGVudGlhbExpbmtDb2RlQ29tbWl0bWVudDIwMjIiLCJ2YWx1ZSI6IkVpQlJDZVhuUVRxdDY4NGxQbkNhd1ljMFFJRVlIeTZlTjNhR3BlUE9MVlBNTnc9PSJ9LCJpc3N1ZXIiOnsiaWQiOiJkaWQ6aW9uOkVpRFVJMGRhS3J0b0tIWHk0emFFeTZPM0k2Z01xeV95TXRJbDBPZTNtejk4VlEifSwiY29udGVudEhhc2giOnsidHlwZSI6IlZlbG9jaXR5Q29udGVudEhhc2gyMDIwIiwidmFsdWUiOiIwZGYxZjg1OTY4YjdiYWM1YzhlMjNlNGZiZWM1YzY0MGZkOTdhOGQ5NTE1ZDhkYTk3ZDhkZWY4MWMzNzcyMjFmIn0sImNyZWRlbnRpYWxTY2hlbWEiOnsiaWQiOiJodHRwczovL3FhbGliLnZlbG9jaXR5bmV0d29yay5mb3VuZGF0aW9uL3NjaGVtYXMvb3Blbi1iYWRnZS1jcmVkZW50aWFsLnNjaGVtYS5qc29uIiwidHlwZSI6Ikpzb25TY2hlbWFWYWxpZGF0b3IyMDE4In0sInZuZlByb3RvY29sVmVyc2lvbiI6MSwiQGNvbnRleHQiOlsiaHR0cHM6Ly93d3cudzMub3JnLzIwMTgvY3JlZGVudGlhbHMvdjEiLCJodHRwczovL3FhbGliLnZlbG9jaXR5bmV0d29yay5mb3VuZGF0aW9uL2NvbnRleHRzL2NyZWRlbnRpYWwtZXh0ZW5zaW9ucy0yMDIyLmpzb25sZC5qc29uIl0sImNyZWRlbnRpYWxTdWJqZWN0Ijp7InR5cGUiOlsiQWNoaWV2ZW1lbnRTdWJqZWN0Il0sImFjaGlldmVtZW50Ijp7ImlkIjoidXJuOnV1aWQ6YmQ2ZDkzMTYtZjdhZS00MDczLWExZTUtMmY3ZjViZDIyOTIyIiwidHlwZSI6WyJBY2hpZXZlbWVudCJdLCJpc3N1ZXIiOiJkaWQ6aW9uOkVpRFVJMGRhS3J0b0tIWHk0emFFeTZPM0k2Z01xeV95TXRJbDBPZTNtejk4VlEiLCJuYW1lIjoiaXNzdWVyLmlkID0gaWRlbnRpZmllciAoMy4wKSIsImRlc2NyaXB0aW9uIjoiY3JlZGVudGlhbFN1YmplY3QuYWNoaWV2ZW1lbnQuaXNzdWVyID0gaXNzdWVyLmlkLiBUcnVzdGVkIElzc3VlciBjaGVjayBzaG91bGQgUEFTUyIsImNyaXRlcmlhIjp7InR5cGUiOiJDcml0ZXJpYSIsIm5hcnJhdGl2ZSI6ImNyZWRlbnRpYWxTdWJqZWN0LmFjaGlldmVtZW50Lmlzc3VlciA9IGlzc3Vlci5pZC4gVHJ1c3RlZCBJc3N1ZXIgY2hlY2sgc2hvdWxkIFBBU1MifSwiaW1hZ2UiOnsiaWQiOiJodHRwczovL3czYy1jY2cuZ2l0aHViLmlvL3ZjLWVkL3BsdWdmZXN0LTItMjAyMi9pbWFnZXMvSkZGLVZDLUVEVS1QTFVHRkVTVDItYmFkZ2UtaW1hZ2UucG5nIiwidHlwZSI6IkltYWdlIn19LCJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSJdfX0sIm5iZiI6MTcwMzE0NDIyMSwianRpIjoiZGlkOnZlbG9jaXR5OnYyOjB4NTc1NzJjZWE1NmZiNDAzZGMxM2M2MGNiYmM2MzcxNTAwZTExNDY1NjoyMzQ1Nzc5NDAyMzYzNjA6ODExIiwiaXNzIjoiZGlkOmlvbjpFaURVSTBkYUtydG9LSFh5NHphRXk2TzNJNmdNcXlfeU10SWwwT2UzbXo5OFZRIiwiaWF0IjoxNzAzMTQ0MjIxfQ.bN0jjREfyWqcol3RWxKg3pkCHd6wmsqwTQl_7TIZ4DEHqCKo589b1nDi96KBrh7gbw7uuvBJQ7cXT1HO6n5zlA"

        const val JwtCredentialOpenBadgeInvalid =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzI1NksiLCJraWQiOiJkaWQ6dmVsb2NpdHk6djI6MHg1NzU3MmNlYTU2ZmI0MDNkYzEzYzYwY2JiYzYzNzE1MDBlMTE0NjU2OjIzNDU3Nzk0MDIzNjM2MDo3NzU2I2tleS0xIn0.eyJ2YyI6eyJ0eXBlIjpbIk9wZW5CYWRnZVYyLjAiLCJWZXJpZmlhYmxlQ3JlZGVudGlhbCJdLCJpZCI6ImRpZDp2ZWxvY2l0eTp2MjoweDU3NTcyY2VhNTZmYjQwM2RjMTNjNjBjYmJjNjM3MTUwMGUxMTQ2NTY6MjM0NTc3OTQwMjM2MzYwOjc3NTYiLCJjcmVkZW50aWFsU3RhdHVzIjp7InR5cGUiOiJWZWxvY2l0eVJldm9jYXRpb25MaXN0SmFuMjAyMSIsImlkIjoiZXRoZXJldW06MHgxNDk5Yzg4ODQwOUQ2MmI4ZUM1MDE2RjRFMmI2YmI0YmVFMUVERjQ0L2dldFJldm9rZWRTdGF0dXM_YWRkcmVzcz0weDU3NTcyY0VBNTZGQjQwM0RjMTNjNjBDYmJDNjM3MTUwMGUxMTQ2NTYmbGlzdElkPTEyNjYxODc1MDI5MzAyNiZpbmRleD0xMCIsInN0YXR1c0xpc3RJbmRleCI6MTAsInN0YXR1c0xpc3RDcmVkZW50aWFsIjoiZXRoZXJldW06MHgxNDk5Yzg4ODQwOUQ2MmI4ZUM1MDE2RjRFMmI2YmI0YmVFMUVERjQ0L2dldFJldm9rZWRTdGF0dXM_YWRkcmVzcz0weDU3NTcyY0VBNTZGQjQwM0RjMTNjNjBDYmJDNjM3MTUwMGUxMTQ2NTYmbGlzdElkPTEyNjYxODc1MDI5MzAyNiJ9LCJsaW5rQ29kZUNvbW1pdG1lbnQiOnsidHlwZSI6IlZlbG9jaXR5Q3JlZGVudGlhbExpbmtDb2RlQ29tbWl0bWVudDIwMjIiLCJ2YWx1ZSI6IkVpQkFZRklBeUthNjFwNUhKeWR0SldHaGlTSXZvbHVTd3JzZ1h6dUtYRXJycFE9PSJ9LCJpc3N1ZXIiOnsiaWQiOiJkaWQ6aW9uOkVpRFVJMGRhS3J0b0tIWHk0emFFeTZPM0k2Z01xeV95TXRJbDBPZTNtejk4VlEifSwiY29udGVudEhhc2giOnsidHlwZSI6IlZlbG9jaXR5Q29udGVudEhhc2gyMDIwIiwidmFsdWUiOiJjMWI3YjUzMWQ5MGU1ZmU1NjU0OTY4M2NhNmI0YjJiMWZlMDdjYjU3N2E5NjQ0ODkyYTZjM2E5YmNhMDZiY2FmIn0sImNyZWRlbnRpYWxTY2hlbWEiOnsiaWQiOiJodHRwczovL3FhbGliLnZlbG9jaXR5bmV0d29yay5mb3VuZGF0aW9uL3NjaGVtYXMvb3Blbi1iYWRnZS12Mi4wLnNjaGVtYS5qc29uIiwidHlwZSI6Ikpzb25TY2hlbWFWYWxpZGF0b3IyMDE4In0sInZuZlByb3RvY29sVmVyc2lvbiI6MSwiQGNvbnRleHQiOlsiaHR0cHM6Ly93d3cudzMub3JnLzIwMTgvY3JlZGVudGlhbHMvdjEiLCJodHRwczovL3FhbGliLnZlbG9jaXR5bmV0d29yay5mb3VuZGF0aW9uL2NvbnRleHRzL2NyZWRlbnRpYWwtZXh0ZW5zaW9ucy0yMDIyLmpzb25sZC5qc29uIl0sImNyZWRlbnRpYWxTdWJqZWN0Ijp7Imhhc0NyZWRlbnRpYWwiOnsibmFtZSI6Imlzc3Vlci5pZCAhPSBpZGVudGlmaWVyIE9CVjIuMCIsImltYWdlIjoiaHR0cHM6Ly93d3cuaW1zZ2xvYmFsLm9yZy9zaXRlcy9kZWZhdWx0L2ZpbGVzL0JhZGdlcy9PQnYycDBGaW5hbC9pbWFnZXMvaW1zZ2xvYmFsLWxvZ28ucG5nIiwidHlwZSI6IkJhZGdlQ2xhc3MiLCJkZXNjcmlwdGlvbiI6ImNyZWRlbnRpYWxTdWJqZWN0Lmhhc0NyZWRlbnRpYWwuaXNzdWVyLmlkICE9IGlzc3Vlci5pZCDihpIgVFJVU1RFRF9JU1NVRVI6IEZBSUwiLCJpc3N1ZXIiOnsibmFtZSI6IlVuaXZlcnNhbCBJc3N1ZXIiLCJ0eXBlIjoiUHJvZmlsZSIsImlkIjoiVGVzdCJ9LCJjcml0ZXJpYSI6Imh0dHBzOi8vd3d3LmFldHNvZnQubmV0IiwidGFncyI6WyJzYW1wbGUtdGFnIl19LCJ0eXBlIjoiQmFkZ2UiLCJAY29udGV4dCI6WyJodHRwczovL3d3dy53My5vcmcvMjAxOC9jcmVkZW50aWFscy92MSIsImh0dHBzOi8vcWFsaWIudmVsb2NpdHluZXR3b3JrLmZvdW5kYXRpb24vY29udGV4dHMvbGF5ZXIxLXYxLjEuanNvbmxkLmpzb24iXX19LCJuYmYiOjE3MDMxNDQyMjEsImp0aSI6ImRpZDp2ZWxvY2l0eTp2MjoweDU3NTcyY2VhNTZmYjQwM2RjMTNjNjBjYmJjNjM3MTUwMGUxMTQ2NTY6MjM0NTc3OTQwMjM2MzYwOjc3NTYiLCJpc3MiOiJkaWQ6aW9uOkVpRFVJMGRhS3J0b0tIWHk0emFFeTZPM0k2Z01xeV95TXRJbDBPZTNtejk4VlEiLCJpYXQiOjE3MDMxNDQyMjF9.cExeY-Qu9WaYV9U5mWKDirdIMilVoPmixDgSkZsmOZCBt99vgfHtlPfyAW-Ez3HlcCwK0v5E8xjhV-IixR36NQ"

        const val JwtCredentialsFromRegularIssuer =
            "[\"$JwtCredentialEducationDegreeRegistrationFromRegularIssuer\", \"$JwtCredentialEmploymentPastFromRegularIssuer\"]"

        const val JwtCredentialsFromNotaryIssuer =
            "[\"$JwtCredentialEducationDegreeRegistrationFromNotaryIssuer\", \"$JwtCredentialEmploymentPastFromNotaryIssuer\"]"

        const val JwtCredentialsFromIdentityIssuer =
            "[\"$JwtCredentialEmailFromIdentityIssuer\", \"$JwtCredentialPassportFromIdentityIssuer\", \"$JwtCredentialDriversLicenseFromIdentityIssuer\"]"

        const val JwtCredentialsWithoutSubject =
            "[\"$JwtCredEmailWithoutSubjectJwt\", \"$JwtCredPhoneWithoutSubjectJwt\"]"

        const val JwtCredentialsOpenBadgeValid = "[$JwtCredentialOpenBadgeValid]"

        const val JwtCredentialsOpenBadgeInvalid = "[$JwtCredentialOpenBadgeInvalid]"

        const val JwtEmptyCredentials = "[]"
    }
}