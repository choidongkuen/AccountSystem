## AccountSystem
계좌 시스템 구현해보기 💸

Use : Spring, Jpa, MariaDB, Redis , H2

Goal : 계좌 및 거랙 관련 기본적인 계좌 기능을 구현한다.


## 시나리오

``` 
우리 회사는 신규 사업으로 핀테크 사업을 하려고 합니다.
해당 사업을 하려면 제일 먼저 필요한 것은 바로 사용자들 마다의 잔액을 관리할 수 있는 계좌 시스템입니다.
계좌 시스템이 있어야 사용자의 잔액을 우리 회사에서 계속 관리하고 사용자들은 그 잔액을 쓰기 위해 우리의 핀테크 시스템을 자연스럽게 사용하게 됩니다.

계좌 시스템을 만들어주세요!
```

## 시나리오 구체화

```
Account(계좌) 시스템은 사용자의 계좌의 정보를 저장하고 있으며, 
외부 시스템에서 거래를 요청한 경우 거래 정보를 받아서 계좌에서 잔액을 거래금액만큼 줄이거나(결제), 
거래 금액만큼 늘리는(결제 취소) 거래 관리 기능을 제공하는 시스템입니다.

크게 사용자, 계좌, 거래의 정보를 저장해야합니다.

사용자는 신규 등록, 해지, 중지, 사용자 정보 조회 등의 기능을 제공해야 하지만
최초 버전에는 빠른 서비스 오픈을 위해 사용자 등록, 해지 ,중지 기능은 제공하지 않고 DB 로 수기 입력을 합니다.

계좌는 계좌 추가, 해지, 확인 기능을 제공합니다.
한 사용자는 최대 10개의 계좌를 가질 수 있고 그 이상의 계좌는 생성하지 못합ㅇㄴ니다.
계좌 번호는 10자리의 정수로 이루어지며 중복이 불가능합니다.
빠르고 안정적인 진행을 위해 계좌번호는 순차적으로 증가하도록 합니다.

거래는 잔액 사용, 잔액 사용 취소, 거래 확인 기능을 제공합니다.
```


## API 명세서

### 계좌

- 계좌 생성 API 💰
- POST/account
- Parameter : 사용자 아이디, 초기 잔액
- 정책 : 사용자가 없는 경우, 계좌가 10개 인 경우 실패 응답
- 성공 응답 : 사용자 아이디, 계좌 번호, 등록 일시

<br>

- 계좌 해지 API 💰
- DELETE/account
- Parameter : 사용자 아이디, 계좌 번호
- 정책 : 사용자 또는 계좌가 존재하지 않는 경우, 사용자 아이디와 계좌 소유주가 다른 경우, 계좌가 이미 해지 상태인 경우, 잔액이 있는 경우 실패 응답
- 성공 응답 : 사용자 아이디, 계좌 번호, 해지 일시

<br>

- 계좌 확인 API 💰
- GET/account?user_id = {userId}
- Parameter : 사용자 아이디
- 정책 : 사용자가 없는 경우 실패 응답
- 성공 응답 : List<계좌 번호> 구조로 응답

<br>

### 거래


- 잔액 사용 API 💵
- POST/transaction/use
- Parameter : 사용자 아이디, 계좌 번호, 거래 금액
- 정책 : 사용자 없는 경우, 사용자 아이디와 계좌 소유주가 다른 경우, 계좌가 이미 해지 상태인 경우, 거래 금액이 잔액보다 큰 경우, 거래 금액이 너무 작거나 큰 경우 실패 응답
- 성공 응답 : 계좌 번호, 거래 결과 코드(성공/실패) , 거래 아이디, 거래 금액, 거래 일시

<br>

- 잔액 사용 취소 API 💵
- POST/transaction/cancel
- Paramter : 거래 아이디, 취소 요청 금액
- 정책 : 거래 아이디에 해당하는 거래가 없는 경우, 거래 금액과 거래 취소 금액이 다른 경우 실패 응답
- 성공 응답 : 계좌 번호, 거래 결과 코드(성공/실패) , 거래 아이디, 거래 금액, 거래 일시

<br>

- 거래 확인 API 💵
- GET/transaction/{transcationId}
- Parameter : 거래 아이디
- 정책 : 해당 거래 아이디의 거래가 없는 경우 실패 응답
- 성공 응답 : 계좌 번호, 거래 종류, 거래 결과 코드, 거래 아이디, 거래 금액, 
