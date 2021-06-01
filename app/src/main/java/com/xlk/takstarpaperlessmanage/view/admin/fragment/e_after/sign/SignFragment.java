package com.xlk.takstarpaperlessmanage.view.admin.fragment.e_after.sign;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
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
import com.mogujie.tt.protobuf.InterfaceMeet;
import com.mogujie.tt.protobuf.InterfaceMember;
import com.mogujie.tt.protobuf.InterfaceRoom;
import com.mogujie.tt.protobuf.InterfaceSignin;
import com.xlk.takstarpaperlessmanage.App;
import com.xlk.takstarpaperlessmanage.R;
import com.xlk.takstarpaperlessmanage.adapter.SignInAdapter;
import com.xlk.takstarpaperlessmanage.base.BaseFragment;
import com.xlk.takstarpaperlessmanage.model.Constant;
import com.xlk.takstarpaperlessmanage.model.EventMessage;
import com.xlk.takstarpaperlessmanage.model.EventType;
import com.xlk.takstarpaperlessmanage.model.GlobalValue;
import com.xlk.takstarpaperlessmanage.model.bean.PdfSignBean;
import com.xlk.takstarpaperlessmanage.model.bean.SignInBean;
import com.xlk.takstarpaperlessmanage.ui.RvItemDecoration;
import com.xlk.takstarpaperlessmanage.util.DateUtil;
import com.xlk.takstarpaperlessmanage.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author Created by xlk on 2021/5/27.
 * @desc
 */
public class SignFragment extends BaseFragment<SignPresenter> implements SignContract.View {
    private LinearLayout rootView;
    private RecyclerView rvContent;
    private TextView tvDue;
    private TextView tvAlready;
    private TextView tvUnchecked;
    private SignInAdapter signInAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_sign;
    }

    @Override
    protected void initView(View inflate) {
        rootView = (LinearLayout) inflate.findViewById(R.id.root_view);
        rvContent = (RecyclerView) inflate.findViewById(R.id.rv_content);
        tvDue = (TextView) inflate.findViewById(R.id.tv_due);
        tvAlready = (TextView) inflate.findViewById(R.id.tv_already);
        tvUnchecked = (TextView) inflate.findViewById(R.id.tv_unchecked);
        inflate.findViewById(R.id.btn_delete).setOnClickListener(v -> {
            List<Integer> checkIds = signInAdapter.getCheckIds();
            if (checkIds.isEmpty()) {
                ToastUtil.showShort(R.string.please_choose_member_first);
                return;
            }
            presenter.deleteSignIn(checkIds);
        });
        inflate.findViewById(R.id.btn_export).setOnClickListener(v -> {
            exportPdf(presenter.getPdfData());
        });
    }

    private void exportPdf(PdfSignBean pdfSignBean) {
        App.threadPool.execute(() -> {
            try {
                long l = System.currentTimeMillis();
                InterfaceMeet.pbui_Item_MeetMeetInfo meetInfo = pdfSignBean.getMeetInfo();
                InterfaceRoom.pbui_Item_MeetRoomDetailInfo roomInfo = pdfSignBean.getRoomInfo();
                List<SignInBean> signInBeans = pdfSignBean.getSignInBeans();
                final int size = signInBeans.size();
                LogUtils.i(TAG, "exportPdf signInBeans.size=" + size);
                int signInCount = pdfSignBean.getSignInCount();
                if (meetInfo == null || roomInfo == null) {
                    return;
                }
                FileUtils.createOrExistsDir(Constant.export_dir);
//                File file = new File(Constant.DIR_EXPORT + "签到信息.pdf");
//                if (file.exists()) {
//                    boolean delete = file.delete();
//                    LogUtils.i(TAG, "exportPdf 删除了原文件=" + delete);
//                }

                Document document = new Document(PageSize.A4);
                PdfWriter.getInstance(document, new FileOutputStream(Constant.export_dir + "签到信息.pdf"));
                document.open();
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
                //设置文字居中 0靠左 1，居中 2，靠右
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
                    LogUtils.i(TAG, "exportPdf isNoSign=" + isNoSign);
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
                            LogUtils.i(TAG, "exportPdf 有图片签到数据");
                            continue;
                        }
                    }
                    LogUtils.i(TAG, "exportPdf 没有图片签到数据，给一个空白");
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
                LogUtils.i(TAG, "exportPdf 用时=" + (System.currentTimeMillis() - l));
                EventBus.getDefault().post(new EventMessage.Builder()
                        .type(EventType.BUS_EXPORT_SUCCESSFUL)
                        .objects("Constant.export_dir" + "签到信息.pdf")
                        .build());
            } catch (DocumentException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected SignPresenter initPresenter() {
        return new SignPresenter(this);
    }

    @Override
    protected void initial() {
        presenter.queryMember();
    }

    @Override
    public void updateSignList(int signInCount) {
        if (signInAdapter == null) {
            signInAdapter = new SignInAdapter(presenter.signInBeans);
            rvContent.setLayoutManager(new LinearLayoutManager(getContext()));
            rvContent.addItemDecoration(new RvItemDecoration(getContext()));
            rvContent.setAdapter(signInAdapter);
            signInAdapter.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                    signInAdapter.check(presenter.signInBeans.get(position).getMember().getPersonid());
                }
            });
        } else {
            signInAdapter.notifyDataSetChanged();
        }
        tvDue.setText(getString(R.string.sign_due_, presenter.signInBeans.size()));
        tvAlready.setText(getString(R.string.already_checked_in_, signInCount));
        tvUnchecked.setText(getString(R.string.unchecked_, presenter.signInBeans.size() - signInCount));
    }
}
