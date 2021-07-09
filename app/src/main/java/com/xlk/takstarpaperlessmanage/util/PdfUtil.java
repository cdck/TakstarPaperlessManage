package com.xlk.takstarpaperlessmanage.util;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.protobuf.ByteString;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.mogujie.tt.protobuf.InterfaceFilescorevote;
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.mogujie.tt.protobuf.InterfaceSignin;
import com.xlk.takstarpaperlessmanage.App;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.model.bean.PdfSignBean;
import com.xlk.takstarpaperlessmanage.model.bean.ScoreMember;
import com.xlk.takstarpaperlessmanage.model.bean.SignInBean;
import com.xlk.takstarpaperlessmanage.model.bean.SubmitMember;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.d_in.rate.PdfRateInfo;
import com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.vote.PdfVoteInfo;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * @author Created by xlk on 2021/7/3.
 * @desc https://blog.csdn.net/weixin_37848710/article/details/89522862
 */
public class PdfUtil {

    private static String createFilePath(String filePath) {
        boolean fileExists = FileUtils.isFileExists(filePath + ".pdf");
        if (fileExists) {
            return createFilePath(filePath + "_" + DateUtil.nowDate());
        }
        return filePath + ".pdf";
    }

    /**
     * 导出签到信息
     *
     * @param dirPath  保存的目录
     * @param fileName 无后缀的文件名
     */
    public static void exportSignIn(String dirPath, String fileName, PdfSignBean pdfSignBean) {
        App.threadPool.execute(() -> {
            InterfaceMeet.pbui_Item_MeetMeetInfo meetInfo = pdfSignBean.getMeetInfo();
            InterfaceRoom.pbui_Item_MeetRoomDetailInfo roomInfo = pdfSignBean.getRoomInfo();
            List<SignInBean> signInBeans = pdfSignBean.getSignInBeans();
            final int size = signInBeans.size();
            LogUtils.i("exportSignIn signInBeans.size=" + size);
            int signInCount = pdfSignBean.getSignInCount();
            if (meetInfo == null || roomInfo == null) {
                return;
            }
            String filePath = createFilePath(dirPath + "/" + fileName);
            try {
                FileUtils.createOrExistsFile(filePath);
                Document document = new Document(PageSize.A4);
                PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();
                /* **** **  配置字体样式  ** **** */
                BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                Font boldFont14 = new Font(bfChinese, 14, Font.NORMAL);
                Font boldFont16 = new Font(bfChinese, 16, Font.NORMAL);

                String top = "会议名称：" + meetInfo.getName().toStringUtf8()
                        + "\n会场：" + meetInfo.getRoomname().toStringUtf8() + "  会场地址：" + roomInfo.getAddr().toStringUtf8()
                        + "\n会议保密：" + (meetInfo.getSecrecy() == 1 ? "是" : "否")
                        + "\n开始时间：" + DateUtil.secondsFormat(meetInfo.getStartTime(), "yyyy/MM/dd HH:mm:ss")
                        + "  结束时间：" + DateUtil.secondsFormat(meetInfo.getEndTime(), "yyyy/MM/dd HH:mm:ss")
                        + "\n应到：" + size + "人  已签到：" + signInCount + "人  未签到：" + (size - signInCount) + "人";
                Paragraph title = new Paragraph(top, boldFont16);
                //设置文字对齐方式： 0靠左， 1居中， 2靠右
                title.setAlignment(1);
                //设置段落上空白
                title.setSpacingBefore(5f);
                //设置段落下空白
                title.setSpacingAfter(10f);
                document.add(title);

                // 添加虚线
                Paragraph dottedLine = new Paragraph();
                dottedLine.add(new Chunk(new DottedLineSeparator()));
                //设置段落上空白
                dottedLine.setSpacingBefore(10f);
                //设置段落下空白
                dottedLine.setSpacingAfter(10f);
                document.add(dottedLine);

                //创建一个两列的表格，添加第三个的时候会自动换相应的行数
                PdfPTable pdfPTable = new PdfPTable(2);
                //设置表格宽度比例为100%
                pdfPTable.setWidthPercentage(100);
                //设置表格默认为无边框
                pdfPTable.getDefaultCell().setBorder(0);
                //定义单元格的高度
                final float cellHeight = 100f;
                for (int i = 0; i < size; i++) {
                    SignInBean item = signInBeans.get(i);
                    InterfaceMember.pbui_Item_MemberDetailInfo member = item.getMember();
                    InterfaceSignin.pbui_Item_MeetSignInDetailInfo sign = item.getSign();
                    boolean isNoSign = sign == null;
                    LogUtils.i("exportPdf isNoSign=" + isNoSign);
                    String content = "参会人：" + member.getName().toStringUtf8()
                            + "\n签到时间：" + (isNoSign ? "" : DateUtil.secondsFormat(sign.getUtcseconds(), "yyyy/MM/dd HH:mm:ss"))
                            + "\n签到状态：" + (isNoSign ? "未签到" : "已签到")
                            + "\n签到方式：" + (isNoSign ? "" : Constant.getMeetSignInTypeName(sign.getSigninType()));
                    Paragraph paragraph = new Paragraph(content, boldFont14);
                    PdfPCell cell_1 = new PdfPCell(paragraph);
                    //设置固定高度
                    cell_1.setFixedHeight(cellHeight);
                    //设置垂直居中
                    cell_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    pdfPTable.addCell(cell_1);

                    if (!isNoSign) {
                        byte[] bytes = sign.getPsigndata().toByteArray();
                        if (bytes != null && bytes.length > 0) {
                            PdfPCell cell_2 = new PdfPCell();
                            //设置固定高度
                            cell_2.setFixedHeight(cellHeight);
                            Image image = Image.getInstance(bytes);
                            image.scaleAbsolute(100, 50);
                            cell_2.setImage(image);
                            pdfPTable.addCell(cell_2);
                            LogUtils.i("exportPdf 有图片签到数据");
                            continue;
                        }
                    }
                    LogUtils.i("exportPdf 没有图片签到数据，给一个空白");
                    //没有图片签到数据时，给一个空白
                    Paragraph a = new Paragraph("");
                    PdfPCell cell_2 = new PdfPCell(a);
                    //设置垂直居中
                    cell_2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    //设置水平居中
                    cell_2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    //设置固定高度
                    cell_2.setFixedHeight(cellHeight);
                    pdfPTable.addCell(cell_2);
                }
                document.add(pdfPTable);
                document.close();
                EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_EXPORT_SUCCESSFUL).objects(filePath).build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void exportVote(String dirPath, String fileName, PdfVoteInfo info) {
        App.threadPool.execute(() -> {
            String filePath = createFilePath(dirPath + "/" + fileName);
            try {
                FileUtils.createOrExistsFile(filePath);
                /* **** **  1.新建document对象  ** **** */
                Document document = new Document(PageSize.A4);
                /* **** **  2.建立一个书写器(Writer)与document对象关联  ** **** */
                PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(filePath));
                /* **** **  3.写入数据之前要打开文档  ** **** */
                document.open();
                /* **** **  4.向文档中添加内容 document.add(); ** **** */
                //新建字体
                BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
                //字体设置：参数一：新建好的字体；参数二：字体大小，参数三：字体样式，多个样式用“|”分隔
                Font boldFont10 = new Font(bfChinese, 10, Font.BOLD);
                Font boldFont12 = new Font(bfChinese, 10, Font.NORMAL);
                Font boldFont14 = new Font(bfChinese, 14, Font.NORMAL);

                Paragraph top = new Paragraph("投票信息", boldFont14);
                //设置文字居中 0靠左 1，居中 2，靠右
                top.setAlignment(1);
                //设置段落上空白
                top.setSpacingBefore(5f);
                //设置段落下空白
                top.setSpacingAfter(5f);
                //往文档中添加内容
                document.add(top);

                Paragraph date = new Paragraph("生成日期时间：" + DateUtil.pdfExportTime(), boldFont10);
                //设置文字居中 0靠左 1，居中 2，靠右
                date.setAlignment(1);
                //设置段落上空白
                date.setSpacingBefore(5f);
                //设置段落下空白
                date.setSpacingAfter(5f);
                //往文档中添加内容
                document.add(date);

                Paragraph title = new Paragraph("标题：" + info.getTitle(), boldFont10);
                //设置文字居中 0靠左 1，居中 2，靠右
                title.setAlignment(0);
                //设置段落上空白
                title.setSpacingBefore(5f);
                //设置段落下空白
                title.setSpacingAfter(5f);
                //往文档中添加内容
                document.add(title);

                Paragraph count = new Paragraph("应到：" + info.getYingdao() + "人  实到：" + info.getShidao() + "人  已投：" + info.getYitou() + "人  未投：" + info.getWeitou() + "人", boldFont10);
                //设置文字居中 0靠左 1，居中 2，靠右
                count.setAlignment(0);
                //设置段落上空白
                count.setSpacingBefore(5f);
                //设置段落下空白
                count.setSpacingAfter(10f);
                //往文档中添加内容
                document.add(count);

                //创建一个三列的表格，添加第三个的时候会自动换相应的行数
                PdfPTable table = new PdfPTable(3);
                table.setWidths(new int[]{10, 45, 45});
                //设置表格宽度比例
                table.setWidthPercentage(95);
//                table.setAlignment(Element.ALIGN_CENTER);//居中
//                table.setAutoFillEmptyCells(true);//自动填满
//                table.setBorderWidth((float) 0.1);//表格边框线条宽度
//                table.setPadding(1);//边距:单元格的边线与单元格内容的边距
//                table.setSpacing(0);//间距：单元格与单元格之间的距离
//                table.addCell(new Paragraph("name"), textfont));//添加单元格内容
//                table.endHeaders();//每页都会显示表头

                //设置表格默认为无边框
                table.getDefaultCell().setBorder(0);

                Paragraph title_paragraph0 = new Paragraph("序号", boldFont10);
                PdfPCell title_cell_0 = new PdfPCell(title_paragraph0);
                title_cell_0.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(title_cell_0);

                Paragraph title_paragraph1 = new Paragraph("参会人-提交人-姓名", boldFont10);
                PdfPCell title_cell_1 = new PdfPCell(title_paragraph1);
                title_cell_1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(title_cell_1);

                Paragraph title_paragraph2 = new Paragraph("选择的项", boldFont10);
                PdfPCell title_cell_2 = new PdfPCell(title_paragraph2);
                title_cell_2.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(title_cell_2);

                //定义单元格的高度
                final float cellHeight = 100f;
                for (int i = 0; i < info.getSubmitMembers().size(); i++) {
                    SubmitMember item = info.getSubmitMembers().get(i);
                    InterfaceMember.pbui_Item_MeetMemberDetailInfo memberInfo = item.getMemberInfo();
                    String answer = item.getAnswer();
                    Paragraph paragraph1 = new Paragraph(String.valueOf(i + 1), boldFont12);
                    PdfPCell cell_1 = new PdfPCell(paragraph1);
                    //设置固定高度
//                    cell_1.setFixedHeight(cellHeight);
                    //设置垂直居中
//                    cell_1.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell_1.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell_1);

                    Paragraph paragraph2 = new Paragraph(memberInfo.getMembername().toStringUtf8(), boldFont12);
                    PdfPCell cell_2 = new PdfPCell(paragraph2);
                    //设置固定高度
//                    cell_2.setFixedHeight(cellHeight);
                    //设置垂直居中
//                    cell_2.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    //设置水平居中
                    cell_2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell_2);

                    Paragraph paragraph3 = new Paragraph(answer, boldFont12);
                    PdfPCell cell_3 = new PdfPCell(paragraph3);
                    //设置固定高度
//                    cell_3.setFixedHeight(cellHeight);
                    //设置垂直居中
//                    cell_3.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    //设置水平居中
                    cell_3.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(cell_3);
                }
                document.add(table);
                /* **** **  5.关闭文档  ** **** */
                document.close();
                EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_EXPORT_SUCCESSFUL).objects(filePath).build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public static void exportScore(String dirPath, String fileName, PdfRateInfo info) {
        App.threadPool.execute(() -> {
            try {
                String filePath = createFilePath(dirPath + "/" + fileName);
                FileUtils.createOrExistsFile(filePath);
                /* **** **  1.新建document对象  ** **** */
                Document document = new Document(PageSize.A4);
                /* **** **  2.建立一个书写器(Writer)与document对象关联  ** **** */
                PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(filePath));
                /* **** **  3.写入数据之前要打开文档  ** **** */
                document.open();
                /* **** **  4.向文档中添加内容 document.add(); ** **** */
                //新建字体
                BaseFont bfChinese = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", BaseFont.NOT_EMBEDDED);
//                BaseFont bfChinese = BaseFont.createFont("fangsong.ttf", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
                //字体设置：参数一：新建好的字体；参数二：字体大小，参数三：字体样式，多个样式用“|”分隔
                Font boldFont10 = new Font(bfChinese, 10, Font.BOLD);
                Font normalFont10 = new Font(bfChinese, 10, Font.NORMAL);
                Font boldFont14 = new Font(bfChinese, 14, Font.NORMAL);

                Paragraph top = new Paragraph("评分详情", boldFont14);
                //设置文字居中 0靠左 1，居中 2，靠右
                top.setAlignment(1);
                //设置段落上空白
                top.setSpacingBefore(5f);
                //设置段落下空白
                top.setSpacingAfter(5f);
                //往文档中添加内容
                document.add(top);

                Paragraph date = new Paragraph("生成日期时间：" + DateUtil.pdfExportTime(), boldFont10);
                //设置文字居中 0靠左 1，居中 2，靠右
                date.setAlignment(1);
                //设置段落上空白
                date.setSpacingBefore(5f);
                //设置段落下空白
                date.setSpacingAfter(5f);
                //往文档中添加内容
                document.add(date);

                //创建一个11列的表格，添加第11个的时候会自动换相应的行数
                PdfPTable table = new PdfPTable(11);
                table.setWidths(new int[]{30, 30, 15, 20, 10, 20, 20, 20, 20, 10, 20});
                //设置表格宽度比例
                table.setWidthPercentage(95);
//                table.setAlignment(Element.ALIGN_CENTER);//居中
//                table.setAutoFillEmptyCells(true);//自动填满
//                table.setBorderWidth((float) 0.1);//表格边框线条宽度
//                table.setPadding(1);//边距:单元格的边线与单元格内容的边距
//                table.setSpacing(0);//间距：单元格与单元格之间的距离
//                table.addCell(new Paragraph("name"), textfont));//添加单元格内容
//                table.endHeaders();//每页都会显示表头

                //设置表格默认为无边框
                table.getDefaultCell().setBorder(0);

//                Paragraph title_paragraph0 = new Paragraph("序号", boldFont10);
//                PdfPCell title_cell_0 = new PdfPCell(title_paragraph0);
//                title_cell_0.setHorizontalAlignment(Element.ALIGN_CENTER);
//                table.addCell(title_cell_0);

                Paragraph title_paragraph1 = new Paragraph("评分描述", boldFont10);
                PdfPCell title_cell_1 = new PdfPCell(title_paragraph1);
                title_cell_1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(title_cell_1);

                Paragraph title_paragraph2 = new Paragraph("文件", boldFont10);
                PdfPCell title_cell_2 = new PdfPCell(title_paragraph2);
                title_cell_2.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(title_cell_2);

                PdfPCell title_cell_3 = new PdfPCell(new Paragraph("状态", boldFont10));
                title_cell_3.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(title_cell_3);

                PdfPCell title_cell_4 = new PdfPCell(new Paragraph("应到/实评人数", boldFont10));
                title_cell_4.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(title_cell_4);

                PdfPCell title_cell_5 = new PdfPCell(new Paragraph("记名", boldFont10));
                title_cell_5.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(title_cell_5);

                PdfPCell title_cell_6 = new PdfPCell(new Paragraph("评分1", boldFont10));
                title_cell_6.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(title_cell_6);

                PdfPCell title_cell_7 = new PdfPCell(new Paragraph("评分2", boldFont10));
                title_cell_7.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(title_cell_7);

                PdfPCell title_cell_8 = new PdfPCell(new Paragraph("评分3", boldFont10));
                title_cell_8.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(title_cell_8);

                PdfPCell title_cell_9 = new PdfPCell(new Paragraph("评分4", boldFont10));
                title_cell_9.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(title_cell_9);

                PdfPCell title_cell_10 = new PdfPCell(new Paragraph("总分", boldFont10));
                title_cell_10.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(title_cell_10);

                PdfPCell title_cell_11 = new PdfPCell(new Paragraph("总平均分", boldFont10));
                title_cell_11.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(title_cell_11);

                /* **** **  内容  ** **** */
                InterfaceFilescorevote.pbui_Type_Item_UserDefineFileScore score = info.getScore();

                PdfPCell content_cell_1 = new PdfPCell(new Paragraph(score.getContent().toStringUtf8(), normalFont10));
                content_cell_1.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(content_cell_1);

                PdfPCell content_cell_2 = new PdfPCell(new Paragraph(info.getFileName(), normalFont10));
                content_cell_2.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(content_cell_2);

                PdfPCell content_cell_3 = new PdfPCell(new Paragraph(Constant.getVoteState(score.getVotestate()), normalFont10));
                content_cell_3.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(content_cell_3);

                PdfPCell content_cell_4 = new PdfPCell(new Paragraph(score.getShouldmembernum() + "/" + score.getRealmembernum(), normalFont10));
                content_cell_4.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(content_cell_4);

                PdfPCell content_cell_5 = new PdfPCell(new Paragraph(score.getMode() == 1 ? App.appContext.getString(R.string.yes) : App.appContext.getString(R.string.no), normalFont10));
                content_cell_5.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(content_cell_5);
                //评分内容
                List<ByteString> voteTextList = score.getVoteTextList();
                PdfPCell content_cell_6 = new PdfPCell(new Paragraph(voteTextList.size() > 0 ? voteTextList.get(0).toStringUtf8() : "", normalFont10));
                content_cell_6.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(content_cell_6);

                PdfPCell content_cell_7 = new PdfPCell(new Paragraph(voteTextList.size() > 1 ? voteTextList.get(1).toStringUtf8() : "", normalFont10));
                content_cell_7.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(content_cell_7);

                PdfPCell content_cell_8 = new PdfPCell(new Paragraph(voteTextList.size() > 2 ? voteTextList.get(2).toStringUtf8() : "", normalFont10));
                content_cell_8.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(content_cell_8);

                PdfPCell content_cell_9 = new PdfPCell(new Paragraph(voteTextList.size() > 3 ? voteTextList.get(3).toStringUtf8() : "", normalFont10));
                content_cell_9.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(content_cell_9);
                List<Integer> itemsumscoreList = score.getItemsumscoreList();
                int total = 0;
                for (int i = 0; i < itemsumscoreList.size(); i++) {
                    total += itemsumscoreList.get(i);
                }
                //总分
                PdfPCell content_cell_10 = new PdfPCell(new Paragraph(String.valueOf(total), normalFont10));
                content_cell_10.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(content_cell_10);
                //平均分
                PdfPCell content_cell_11 = new PdfPCell(new Paragraph(score.getSelectcount() > 0 ? String.valueOf(Constant.div(total, score.getSelectcount(), 2)) : "", normalFont10));
                content_cell_11.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(content_cell_11);
                document.add(table);

                /* **** **  提交人表格  ** **** */
                Paragraph submit = new Paragraph("提交人信息", boldFont10);
                //设置文字居中 0靠左 1，居中 2，靠右
                submit.setAlignment(1);
                //设置段落上空白
                submit.setSpacingBefore(5f);
                //设置段落下空白
                submit.setSpacingAfter(5f);
                //往文档中添加内容
                document.add(submit);

                //创建一个5列的表格，添加第5个的时候会自动换相应的行数
                PdfPTable memberTable = new PdfPTable(5);
                memberTable.setWidths(new int[]{30, 30, 15, 15, 40});
                //设置表格宽度比例
                memberTable.setWidthPercentage(95);
//                table.setAlignment(Element.ALIGN_CENTER);//居中
//                table.setAutoFillEmptyCells(true);//自动填满
//                table.setBorderWidth((float) 0.1);//表格边框线条宽度
//                table.setPadding(1);//边距:单元格的边线与单元格内容的边距
//                table.setSpacing(0);//间距：单元格与单元格之间的距离
//                table.addCell(new Paragraph("name"), textfont));//添加单元格内容
//                table.endHeaders();//每页都会显示表头

                //设置表格默认为无边框
                memberTable.getDefaultCell().setBorder(0);
                //参会人
                PdfPCell memberCell_1 = new PdfPCell(new Paragraph("参会人", boldFont10));
                memberCell_1.setHorizontalAlignment(Element.ALIGN_CENTER);
                memberTable.addCell(memberCell_1);
                //分数
                PdfPCell memberCell_2 = new PdfPCell(new Paragraph("分数", boldFont10));
                memberCell_2.setHorizontalAlignment(Element.ALIGN_CENTER);
                memberTable.addCell(memberCell_2);
                //总分
                PdfPCell memberCell_3 = new PdfPCell(new Paragraph("总分", boldFont10));
                memberCell_3.setHorizontalAlignment(Element.ALIGN_CENTER);
                memberTable.addCell(memberCell_3);
                //平均分
                PdfPCell memberCell_4 = new PdfPCell(new Paragraph("平均分", boldFont10));
                memberCell_4.setHorizontalAlignment(Element.ALIGN_CENTER);
                memberTable.addCell(memberCell_4);
                //评分意见
                PdfPCell memberCell_5 = new PdfPCell(new Paragraph("评分意见", boldFont10));
                memberCell_5.setHorizontalAlignment(Element.ALIGN_CENTER);
                memberTable.addCell(memberCell_5);

                List<ScoreMember> scoreMembers = info.getScoreMembers();
                for (int i = 0; i < scoreMembers.size(); i++) {
                    ScoreMember item = scoreMembers.get(i);
                    InterfaceMember.pbui_Item_MeetMemberDetailInfo memberInfo = item.getMember();
                    InterfaceFilescorevote.pbui_Type_Item_FileScoreMemberStatistic scoreMember = item.getScore();
                    //参会人名称
                    PdfPCell cell_1 = new PdfPCell(new Paragraph(memberInfo.getMembername().toStringUtf8(), normalFont10));
                    cell_1.setHorizontalAlignment(Element.ALIGN_CENTER);
                    memberTable.addCell(cell_1);

                    List<Integer> scoreList = scoreMember.getScoreList();
                    String scoreStr = "";
                    int totalA = 0;
                    for (int a : scoreList) {
                        if (!scoreStr.isEmpty()) {
                            scoreStr += "|";
                        }
                        scoreStr += a;
                        totalA += a;
                    }
                    //评分
                    PdfPCell cell_2 = new PdfPCell(new Paragraph(scoreStr, normalFont10));
                    cell_2.setHorizontalAlignment(Element.ALIGN_CENTER);
                    memberTable.addCell(cell_2);
                    //总分
                    PdfPCell cell_3 = new PdfPCell(new Paragraph(String.valueOf(totalA), normalFont10));
                    cell_3.setHorizontalAlignment(Element.ALIGN_CENTER);
                    memberTable.addCell(cell_3);
                    //平均分
                    double div = Constant.div(totalA, item.getScore().getScoreCount(), 2);
                    PdfPCell cell_4 = new PdfPCell(new Paragraph(String.valueOf(div), normalFont10));
                    cell_4.setHorizontalAlignment(Element.ALIGN_CENTER);
                    memberTable.addCell(cell_4);
                    //评分意见
                    PdfPCell cell_5 = new PdfPCell(new Paragraph(scoreMember.getContent().toStringUtf8(), normalFont10));
                    cell_5.setHorizontalAlignment(Element.ALIGN_CENTER);
                    memberTable.addCell(cell_5);
                }
                document.add(memberTable);
                /* **** **  5.关闭文档  ** **** */
                document.close();
                EventBus.getDefault().post(new EventMessage.Builder().type(EventType.BUS_EXPORT_SUCCESSFUL).objects(filePath).build());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
