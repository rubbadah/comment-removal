package commentremoval;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;
import java.io.BufferedWriter;
import java.util.UUID;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentRemoval {

	public static void main(String[] args) {
		// NOTE: 拡張子は暫定小文字のみ対応とする
		String targExtensionList[] = { ".c", ".h" };

		// フォルダは起動引数より指定
		String direPath = args[0];
		File dir = new File(direPath);

		// ファイル一覧取得
		File[] fileList = dir.listFiles();
		if (fileList == null) {
			// 指定フォルダが存在しない場合、処理終了
			System.out.println("指定フォルダが存在しませんでした。処理を終了します。");
			return;
		}

		for (int i = 0; i < fileList.length; i++) {
			// ファイル名の出力
			System.out.println(String.format("ファイル名：%s", fileList[i].getName()));

			if (fileList[i].isDirectory()) {
				// ディレクトリの場合何もせず次へ
				System.out.println("→フォルダです。");
				continue;
			}

			// ファイルの場合
			if (fileList[i].isFile()) {
				String fileName = fileList[i].getName();

				int extensionIndex = fileName.lastIndexOf(".");
				if (extensionIndex < 0) {
					// 拡張子が存在しない場合何もせず次へ
					System.out.println("→拡張子がありません。");
					continue;
				}

				if (!Arrays.asList(targExtensionList).contains(fileName.substring(extensionIndex))) {
					// 対象外の拡張子の場合何もせず次へ
					System.out.println("→対象外の拡張子です。");
					continue;
				}

				// ファイル読み込み
				List<String> lines = new ArrayList<String>();
				try {
					lines = Files.readAllLines(Paths.get(fileList[i].toString()), StandardCharsets.UTF_8);
				} catch (IOException e) {
					e.printStackTrace();
				}

				// NOTE: 行ごとにループして判定する方法でもいいが、なんとなく正規表現での置換で実装してみる
				String strLines = deleteCommentReg(lines);

				Path releaseDirePath = Paths.get(direPath, "release");
				if (!Files.exists(releaseDirePath)) {
					// releaseフォルダが存在しない場合新規作成
					try {
						Files.createDirectories(releaseDirePath);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				// releaseフォルダへ出力
				Path outFileNamePath = releaseDirePath.resolve(Paths.get(fileList[i].getName()));
				try (BufferedWriter bw = Files.newBufferedWriter(outFileNamePath, StandardCharsets.UTF_8)) {
					bw.write(strLines);
					System.out.println("→コメントを削除しました。");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		System.out.println("------------------------------");
		System.out.println("すべての処理が終了しました。");
	}

	/**
	 * コメント削除処理
	 * 
	 * @param lines: 1行1要素のリスト形式
	 * @return コメント削除済みの文字列（行は\nで結合済み）
	 */
	private static String deleteCommentReg(List<String> lines) {

		String strLines = String.join("\n", lines);

		// ダブルクォートで囲まれた部分を一時的にUUID置き換える（文字列内でコメントの記号が使用されていた場合の誤置換対策）
		// NOTE: ↓まじで終わってる
		Map<String, String> stringMapping = new HashMap<>();
		String stringRegex = "(\"(.|\n)*?\")";
		Pattern stringPattern = Pattern.compile(stringRegex);
		Matcher stringMatcher = stringPattern.matcher(strLines);

		while (stringMatcher.find()) {
			if (!stringMapping.containsKey(stringMatcher.group())) {
				// UUIDに置換済みではない場合
				String dummyStr = String.format("\"%s\"", UUID.randomUUID());
				String prevStr = Pattern.quote(stringMatcher.group());
				stringMapping.put(stringMatcher.group(), dummyStr);
				strLines = strLines.replaceAll(prevStr, dummyStr);
			}
		}

		// ブロックコメント（スラッシュアスタリスク）の削除
		strLines = strLines.replaceAll("[ \t]*/\\*(.|\n)*?\\*/", "");
		// NOTE: 半角スペースorTABが0文字以上、/*、文字or改行が0文字以上、*/

		// 行コメントの削除
		strLines = strLines.replaceAll("[ \t]*//.*\n", "\n");
		// NOTE: 半角スペースorTABが0文字以上、//、文字が0文字以上、改行*/

		// 空行削除
		strLines = strLines.replaceAll("\n{2,}", "\n");
		// NOTE: 改行が2回以上連続（＝文字が入力されずに改行されている行）

		// 文字列を元に戻す
		for (Map.Entry<String, String> entry : stringMapping.entrySet()) {
			// もともと\単発だった物の復元のため
			String prevStr = entry.getKey().replaceAll("\\\\", "\\\\\\\\");
			strLines = strLines.replaceAll(entry.getValue(), prevStr);
		}

		return strLines;
	}
}
