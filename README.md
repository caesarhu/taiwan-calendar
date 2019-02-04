# taiwan-calendar

A Clojure library --> 匯入台灣人事行政總處行事曆 Excel檔，讀取工作日與假日。

## Usage

```clojure
(year-calendar file)         ;; file -> Excel file path

(year-calendar file labor?)  ;; labor? -> 要不要加入勞動節為假日，預設為true
```

輸出結果：

```clojure
{:year int                                ;;年度
 :calendar {:date java-time.local-date    ;;日期
            :holiday? bool}               ;;是否假日?
 :make-up                                 ;;補上班日
 :long-weekend                            ;;連續假日
}
```



## License

Copyright © 2019 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
