package jp.hishidama.swing.layout;

import java.awt.Component;
import java.awt.Container;
import java.util.Iterator;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.GroupLayout.Group;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;

/**
 * GroupLayout���[�e�B���e�B�[.
 *
 * <p>
 * �R���|�[�l���g�̓񎟌��z���{@link javax.swing.GroupLayout GroupLayout}�Ŕz�u����B<br>
 * ��<a target="hishidama" href="http://www.ne.jp/asahi/hishidama/home/tech/soft/java/swing/GroupLayoutUtil.html"
 * >�g�p��</a>
 * </p>
 * <p>
 * �����N���X�̓C���X�^���X�t�B�[���h�Ɍʂ̒l��ێ�����̂ŁAMT�Z�[�t�ł͂Ȃ��B
 * </p>
 *
 * @author <a target="hishidama" href="http://www.ne.jp/asahi/hishidama/home/tech/soft/java/swing/GroupLayoutUtil.html"
 *         >�Ђ�����</a>
 * @since 2009.11.06
 * @version 2009.11.07
 */
public class GroupLayoutUtil {

	/** �z��̍����Ɠ����R���|�[�l���g��z�u���� */
	public static final Component SAME_L = new JLabel("���Ɠ���");
	/** �z��̏㑤�Ɠ����R���|�[�l���g��z�u���� */
	public static final Component SAME_U = new JLabel("��Ɠ���");

	/**
	 * �M���b�v�w��R���|�[�l���g.
	 * <p>
	 * �R���|�[�l���g�̍�����я�ɃM���b�v��ǉ�����ׂ̃R���|�[�l���g�B<br>
	 * ��<a target="hishidama" href="http://www.ne.jp/asahi/hishidama/home/tech/soft/java/swing/GroupLayoutUtil.html#h_sample_gap"
	 * >�g�p��</a>
	 * </p>
	 *
	 * @author <a target="hishidama" href="http://www.ne.jp/asahi/hishidama/home/tech/soft/java/swing/GroupLayoutUtil.html"
	 *         >�Ђ�����</a>
	 * @since 2009.11.07
	 */
	public static class Gap extends Component {
		private static final long serialVersionUID = -3143356632258067324L;
		protected Component comp;
		protected int colMin, colPref, colMax;
		protected int rowMin, rowPref, rowMax;

		/**
		 * �R���X�g���N�^�[.
		 *
		 * @param comp
		 *            �R���|�[�l���g
		 * @param colSize
		 *            ��M���b�v�T�C�Y�i���̂Ƃ��̓M���b�v��ǉ����Ȃ��j
		 * @param rowSize
		 *            �s�M���b�v�T�C�Y�i���̂Ƃ��̓M���b�v��ǉ����Ȃ��j
		 * @see SequentialGroup#addGap(int)
		 */
		public Gap(Component comp, int colSize, int rowSize) {
			this(comp, colSize, colSize, colSize, rowSize, rowSize, rowSize);
		}

		/**
		 * �R���X�g���N�^�[.
		 *
		 * @param comp
		 *            �R���|�[�l���g
		 * @param colMin
		 *            ��M���b�v�ŏ��T�C�Y
		 * @param colPref
		 *            ��M���b�v�����T�C�Y�i���̂Ƃ��̓M���b�v��ǉ����Ȃ��j
		 * @param colMax
		 *            ��M���b�v�ő�T�C�Y
		 * @param rowMin
		 *            �s�M���b�v�ŏ��T�C�Y
		 * @param rowPref
		 *            �s�M���b�v�����T�C�Y�i���̂Ƃ��̓M���b�v��ǉ����Ȃ��j
		 * @param rowMax
		 *            �s�M���b�v�ő�T�C�Y
		 * @see SequentialGroup#addGap(int, int, int)
		 */
		public Gap(Component comp, int colMin, int colPref, int colMax,
				int rowMin, int rowPref, int rowMax) {
			this.comp = comp;
			this.colMin = colMin;
			this.colPref = colPref;
			this.colMax = colMax;
			this.rowMin = rowMin;
			this.rowPref = rowPref;
			this.rowMax = rowMax;
		}

		/**
		 * �R���|�[�l���g�擾.
		 *
		 * @return �R���|�[�l���g
		 */
		public Component getComponent() {
			return comp;
		}
	}

	protected Component[][] components;
	protected int xsize, ysize;

	protected GroupLayout groupLayout;

	protected CreateColGroup colCreator = new CreateColGroup();
	protected CreateRowGroup rowCreator = new CreateRowGroup();

	/**
	 * �R���X�g���N�^�[.
	 */
	public GroupLayoutUtil() {
	}

	/**
	 * �R���|�[�l���g�z��ݒ�.
	 *
	 * @param components
	 *            �R���|�[�l���g�̓񎟌��z��
	 */
	public void setComponents(Component[][] components) {
		this.components = components;
		this.ysize = components.length;
		this.xsize = 0;
		for (int i = 0; i < ysize; i++) {
			Component[] line = components[i];
			xsize = Math.max(xsize, line.length);
		}
	}

	/**
	 * �R���|�[�l���g�z��擾.
	 *
	 * @return �R���|�[�l���g�̓񎟌��z��
	 */
	public Component[][] getComponents() {
		return components;
	}

	/**
	 * �R���|�[�l���g�z��̉������̌����擾.
	 *
	 * @return �z��
	 */
	public int getXSize() {
		return xsize;
	}

	/**
	 * �R���|�[�l���g�z��̏c�����̌����擾.
	 *
	 * @return �z��
	 */
	public int getYSize() {
		return ysize;
	}

	/**
	 * �R���|�[�l���g�擾.
	 *
	 * @param x
	 *            X
	 * @param y
	 *            Y
	 * @return �R���|�[�l���g�i�z��͈̔͊O�̏ꍇ��null�j
	 */
	public Component getComponent(int x, int y) {
		if (y < 0 || y >= ysize || x < 0) {
			return null;
		}
		Component[] line = components[y];
		if (x >= line.length) {
			return null;
		}
		return line[x];
	}

	/**
	 * GroupLayout�ݒ�.
	 *<p>
	 * �R���|�[�l���g��z�u����GroupLayout�𐶐����A�w�肳�ꂽ�R���e�i�ɓo�^����B
	 * </p>
	 *
	 * @param container
	 *            �R���e�i
	 */
	public void setGroupLayoutTo(Container container) {
		groupLayout = createGroupLayout(container);
		initGroupLayout(groupLayout);
		{
			Group cols = colCreator.createGroup(0, xsize);
			groupLayout.setHorizontalGroup(cols);
		}
		{
			Group rows = rowCreator.createGroup(0, ysize);
			groupLayout.setVerticalGroup(rows);
		}
		container.setLayout(groupLayout);
	}

	/**
	 * GroupLayout�C���X�^���X����.
	 *
	 * @param container
	 *            �R���e�i
	 * @return GroupLayout
	 * @see #setGroupLayoutTo(Container)
	 */
	protected GroupLayout createGroupLayout(Container container) {
		return new GroupLayout(container);
	}

	/**
	 * GroupLayout������.
	 *
	 * @param layout
	 *            GroupLayout
	 */
	protected void initGroupLayout(GroupLayout layout) {
		// ��
		// �R���|�[�l���g���m�̊Ԋu���󂯂�ݒ�
		// layout.setAutoCreateGaps(true);
		// layout.setAutoCreateContainerGaps(true);
	}

	/**
	 * GroupLayout�擾.
	 *<p>
	 * {@link #setGroupLayoutTo(Container)}���Ă�ł���łȂ��ƁAnull���Ԃ�B
	 * </p>
	 *
	 * @return GroupLayout
	 * @see #setGroupLayoutTo(Container)
	 */
	public GroupLayout getGroupLayout() {
		return groupLayout;
	}

	/**
	 * ���C�A�E�g�z�u�{��.
	 */
	protected abstract class CreateGroup {

		/**
		 * ���C�A�E�g���s.
		 *
		 * @param x
		 *            �J�n�ʒu
		 * @param mx
		 *            �I���ʒu
		 * @return �O���[�v
		 */
		public Group createGroup(int x, int mx) {
			SequentialGroup sg = groupLayout.createSequentialGroup();
			while (x < mx) {
				if (!nextIsSame(x)) {
					// �����E��SAME��1�������Ƃ�
					ParallelGroup pg = createGroup1(x);
					if (pg != null) {
						addGap(sg, x);
						sg.addGroup(pg);
					}
					x++;
					continue;
				}

				// �����E��SAME��1�ł�����Ƃ�
				int sx = lastSame(x + 1, mx);
				if (sx < 0) {
					throw new IllegalStateException();
				}
				Group ng = createGroup(x + 1, sx + 1);

				ParallelGroup pg = createGroup1(x, true); // �E��SAME����
				ParallelGroup pg0 = createGroup1(x, false); // �E��SAME�Ȃ�
				if (pg0 == null) {
					pg0 = createParallelGroup(); // ��̃O���[�v
				}
				SequentialGroup sg0 = groupLayout.createSequentialGroup();
				sg0.addGroup(pg0);
				addGap(sg0, x + 1);
				sg0.addGroup(ng);
				pg.addGroup(sg0);

				addGap(sg, x);
				sg.addGroup(pg);
				x = sx + 1;
			}
			return sg;
		}

		/**
		 * ��񕪂�ParallelGroup���쐬����B
		 *
		 * @param x
		 * @return �O���[�v
		 */
		protected ParallelGroup createGroup1(int x) {
			ParallelGroup pg = null;
			for (int y : getIterable()) {
				Component c = get(x, y);
				if (c == null || c == SAME_U || c == SAME_L) {
					continue;
				}
				if (pg == null) {
					pg = createParallelGroup();
				}
				addComponent(pg, c);
			}
			return pg;
		}

		/**
		 * ��񕪂�ParallelGroup���쐬����B
		 *
		 * @param x
		 * @param nextIsSame
		 *            true�F�E����SAME�̂��̂�����I������B<br>
		 *            false�F�E����SAME�łȂ����̂�����I������B
		 * @return �O���[�v
		 */
		protected ParallelGroup createGroup1(int x, boolean nextIsSame) {
			ParallelGroup pg = null;
			for (int y : getIterable()) {
				Component c = get(x, y);
				if (c == null || c == SAME_U || c == SAME_L) {
					continue;
				}
				Component n = get(x + 1, y);
				if (isSame(n) == nextIsSame) {
					if (pg == null) {
						pg = createParallelGroup();
					}
					addComponent(pg, c);
				}
			}
			return pg;
		}

		/**
		 * �u�������L���ȃR���|�[�l���g�ŁA�E����SAME�ł���v���̂����݂��邩�ǂ����𔻒肷��B
		 *
		 * @param x
		 * @return true�F�E����SAME�ł�����̂����݂���
		 */
		protected boolean nextIsSame(int x) {
			for (int y : getIterable()) {
				Component c = get(x, y);
				if (c == null || c == SAME_U || c == SAME_L) {
					continue;
				}
				Component n = get(x + 1, y);
				if (isSame(n)) {
					return true;
				}
			}
			return false;
		}

		/**
		 * �uSAME��1�ł������v���A�����đ��݂���A��ԉE�̗�̈ʒu��Ԃ��B
		 *
		 * @param x
		 *            �J�n�ʒu
		 * @param mx
		 *            �I���ʒu
		 * @return ��ԉE�̈ʒu
		 */
		protected int lastSame(int x, int mx) {
			int last = -1;
			for (int i = x; i < mx; i++) {
				if (existSame(i)) {
					last = i;
					continue;
				}
				break;
			}
			return last;
		}

		protected abstract Component get(int x, int y);

		protected abstract boolean existSame(int x);

		protected abstract boolean isSame(Component c);

		protected abstract Iterable<Integer> getIterable();

		protected abstract ParallelGroup createParallelGroup();

		protected void addComponent(Group group, Component c) {
			if (c instanceof Gap) {
				c = ((Gap) c).getComponent();
			}
			group.addComponent(c);
		}

		protected abstract void addGap(SequentialGroup sg, int x);
	}

	/**
	 * ��̃��C�A�E�g�z�u�N���X.
	 */
	protected class CreateColGroup extends CreateGroup {

		@Override
		protected Component get(int x, int y) {
			return getComponent(x, y);
		}

		@Override
		protected boolean existSame(int x) {
			for (int y : getIterable()) {
				Component c = getComponent(x, y);
				if (isSame(c)) {
					return true;
				}
			}
			return false;
		}

		@Override
		protected boolean isSame(Component c) {
			return c == SAME_L;
		}

		@Override
		protected Iterable<Integer> getIterable() {
			return new Iterable<Integer>() {
				@Override
				public Iterator<Integer> iterator() {
					return new Iterator<Integer>() {
						int i = 0;

						@Override
						public boolean hasNext() {
							return i < ysize;
						}

						@Override
						public Integer next() {
							return i++;
						}

						@Override
						public void remove() {
							throw new UnsupportedOperationException();
						}
					};
				}
			};
		}

		@Override
		protected ParallelGroup createParallelGroup() {
			return groupLayout.createParallelGroup();
		}

		@Override
		protected void addGap(SequentialGroup sg, int x) {
			final int PREF_NONE = -1;
			int min = Integer.MAX_VALUE, pref = PREF_NONE, max = Integer.MIN_VALUE;
			for (int y : getIterable()) {
				Component c = getComponent(x, y);
				if (c instanceof Gap) {
					Gap g = (Gap) c;
					if (g.colPref < 0) {
						continue;
					}
					if (pref > PREF_NONE && pref != g.colPref) {
						String msg = String.format(
								"colPref�s��v(%d,%d) pref=%d/%d", x, y, pref,
								g.colPref);
						throw new IllegalStateException(msg);
					}
					pref = g.colPref;
					min = Math.min(min, g.colMin);
					max = Math.max(max, g.colMax);
				}
			}
			if (pref > PREF_NONE) {
				sg.addGap(min, pref, max);
			}
		}
	}

	/**
	 * �s�̃��C�A�E�g�z�u�N���X.
	 */
	protected class CreateRowGroup extends CreateGroup {

		@Override
		protected Component get(int y, int x) {
			return getComponent(x, y);
		}

		@Override
		protected boolean existSame(int y) {
			for (int x : getIterable()) {
				Component c = getComponent(x, y);
				if (isSame(c)) {
					return true;
				}
			}
			return false;
		}

		@Override
		protected boolean isSame(Component c) {
			return c == SAME_U;
		}

		@Override
		protected Iterable<Integer> getIterable() {
			return new Iterable<Integer>() {
				@Override
				public Iterator<Integer> iterator() {
					return new Iterator<Integer>() {
						int i = 0;

						@Override
						public boolean hasNext() {
							return i < xsize;
						}

						@Override
						public Integer next() {
							return i++;
						}

						@Override
						public void remove() {
							throw new UnsupportedOperationException();
						}
					};
				}
			};
		}

		@Override
		protected ParallelGroup createParallelGroup() {
			return groupLayout
					.createParallelGroup(GroupLayout.Alignment.BASELINE);
		}

		@Override
		protected void addGap(SequentialGroup sg, int y) {
			final int PREF_NONE = -1;
			int min = Integer.MAX_VALUE, pref = PREF_NONE, max = Integer.MIN_VALUE;
			for (int x : getIterable()) {
				Component c = getComponent(x, y);
				if (c instanceof Gap) {
					Gap g = (Gap) c;
					if (g.rowPref < 0) {
						continue;
					}
					if (pref > PREF_NONE && pref != g.rowPref) {
						String msg = String.format(
								"rowPref�s��v(%d,%d) pref=%d/%d", x, y, pref,
								g.rowPref);
						throw new IllegalStateException(msg);
					}
					pref = g.rowPref;
					min = Math.min(min, g.rowMin);
					max = Math.max(max, g.rowMax);
				}
			}
			if (pref > PREF_NONE) {
				sg.addGap(min, pref, max);
			}
		}
	}
}
