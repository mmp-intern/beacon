# 커밋명 작성 규칙

| **컨벤션**      | **설명** |
|--------------|-----------|
| **feat**     | 새로운 기능을 추가하는 경우 |
| **fix**      | 버그를 고친 경우 |
| **docs**     | 문서를 수정한 경우 |
| **style**    | 코드 포맷 변경, 세미콜론 누락, 코드 수정이 없는 경우 |
| **refactor** | 코드 리펙토링 |
| **test**     | 테스트 코드. 리펙토링 테스트 코드를 추가했을 경우 |
| **chore**    | 빌드 업무 수정, 패키지 매니저 수정 |
| **design**   | CSS 등 사용자가 UI 디자인을 변경했을 경우 |
| **rename**   | 파일명(or 폴더명)을 수정한 경우 |
| **remove**   | 코드(파일)의 삭제가 있을 경우 |

# 브랜치 전략

| **브랜치**    | **설명**                                                                         |
|------------|----------------------------------------------------------------------------------|
| **main**   | 제품으로 출시될 수 있는 브랜치. 항상 배포 가능한 상태를 유지.                             |
| **develop** | 다음 출시 버전을 개발하는 브랜치. 기능이 추가되고 통합되는 브랜치.                             |
| **feature** | 기능 개발을 위한 브랜치. develop 브랜치에서 파생되고, 완료되면 develop 브랜치로 병합.       |
| **release** | 이번 출시를 준비하는 브랜치. develop 브랜치에서 파생되며, 버그 수정 및 테스트 후 main과 develop 브랜치로 병합. |
| **hotfix** | 출시 버전에서 발생한 버그를 수정하는 브랜치. main 브랜치에서 파생되며, 수정 후 main과 develop 브랜치로 병합.    |



